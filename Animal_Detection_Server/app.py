
import csv
import os,logging
from flask import Flask, render_template, request, session, redirect, url_for, flash, jsonify
import base64
from flask_mail import Mail, Message
import demjson
from sklearn.model_selection import train_test_split
# import warnings
# warnings.filterwarnings('ignore')
from DBConnection import Db
from werkzeug.utils import secure_filename

static_path = 'C:\\Users\\ADHI V\\PycharmProjects\\Animal_Detection_Server\\static\\'
user_pic_path = 'C:\\Users\\ADHI V\\PycharmProjects\\Animal_Detection_Server\\static\\uploads\\user_image\\'

UPLOAD_FOLDER = '/uploads'
#ALLOWED_EXTENSIONS = {'txt', 'pdf', 'png', 'jpg', 'jpeg', 'gif'}


app = Flask(__name__)

app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
app.secret_key="qwerty"
mail = Mail(app)

# configuration of mail
app.config['MAIL_SERVER']='smtp.gmail.com'
app.config['MAIL_PORT'] = 465
app.config['MAIL_USERNAME'] = 'soundifyapp20@gmail.com'
app.config['MAIL_PASSWORD'] = 'Soundify@123'
app.config['MAIL_USE_TLS'] = False
app.config['MAIL_USE_SSL'] = True
mail = Mail(app)




#def allowed_file(filename):
    #return '.' in filename and \
        #   filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

def auth_check(loginId):
    if loginId == 1 :
        return 1;
    else :
        return 2;



#===============================================   ADMIN MODULE ===========================================
@app.route('/index')
def indx():
    return render_template('admin/index.html')

@app.route('/admin')
def console():
    if 'email' in session:
        username = session['email']
        if username == "admin":
            #return render_template('admin/index.html',data=username)
            return redirect(url_for('addAnimalD'))
        else:
            flash("Sorry, You don't have admin access !! Please login as admin")
            return redirect(url_for('login'))

    else:
        flash("Not logged in, Please login to continue")
        return  redirect(url_for('login'))
@app.route('/admin/changePass')
def changePassword():
    loginId = session['lid']
    if auth_check(loginId) == 1:
        return render_template('admin/change_password.html')
    else:
        return "Access Denied"

@app.route('/admin/changePassFn',methods=['POST'])
def changePasswordFn():
    new_password = request.form['new_password']
    confirm_password = request.form['confirm_password']
    if(new_password == confirm_password):
        db = Db()
        db.update("UPDATE authentication SET password='"+new_password+"' WHERE login_id = 1")
        return "password changed successfully"
    else:
        flash("Passwords dosen't match")
        return redirect(url_for('changePassword'))




@app.route('/admin/view_animalD')
def viewAnimalD():
    loginId = session['lid']
    if auth_check(loginId) == 1:
        db = Db()
        result = db.select("SELECT * FROM animal_database")
        return render_template('admin/view_animal_data.html',data=result)
    else:
        return "Access Denied"


@app.route('/admin/add_animalD')
def addAnimalD():
    loginId = session['lid']
    if auth_check(loginId) == 1:
       return render_template('admin/add_animal_data.html')
    else:
        return "Access Denied"

@app.route('/admin/add_animalDFn',methods=['POST'])
def addAnimalDFn():
    animal_name = request.form['animal_name']
    animal_desc = request.form['animal_desc']
    animal_img  = request.files['img']
    db = Db()
    res = db.insert("INSERT INTO animal_database(name,description,photo) values ('" + animal_name + "','" + animal_desc + "','') ")
    filename = "uploads_"+str(res)+".jpg"
    animal_img.save(static_path+"uploads\\animal_img\\"+filename)
    db.update("UPDATE animal_database SET photo ='"+filename+"' WHERE animal_id="+str(res)+"")

    return "<script>alert('Data added successfully');window.location='/admin/add_animalD';</script>"



@app.route('/admin/viewFeedback')
def Feedback():
    loginId = session['lid']
    if auth_check(loginId) == 1:
        db = Db()
        res = db.select("SELECT * FROM feedback_data INNER JOIN user_data ON feedback_data.user_id = user_data.login_id")
        return render_template('admin/view_feedback.html',data=res)
    else :
        return "Access denied"

@app.route('/admin/delete_animalData/<id>')
def delete_animalData(id):
    loginId = session['lid']
    if auth_check(loginId) == 1:
        db = Db()
        db.delete("delete from animal_database where animal_id='"+id+"'")
        return "data deleted successfully"
    else :
        return "Access denied"

@app.route('/adminedit_animalData/<id>')
def edit_animalData(id):
    loginId = session['lid']
    session['animal_id']=id
    if auth_check(loginId) == 1:
        db = Db()
        res=db.select_one("Select * from animal_database where animal_id='"+id+"'")
        return render_template('/admin/edit_animalData.html',data=res)
    else :
        return "Access denied"

@app.route('/admin/edit_animalDataFn',methods=['POST'])
def edit_animalDataFn():
    ids=session['animal_id']
    animal_name = request.form['animal_name']
    animal_desc = request.form['animal_desc']



    import datetime
    id=str(datetime.datetime.now().year)+str(datetime.datetime.now().month)+str(datetime.datetime.now().day)+str(datetime.datetime.now().hour)+str(datetime.datetime.now().minute)+str(datetime.datetime.now().second)





    db = Db()
    if 'img' in request.files:
        animal_img = request.files['img']
        filename = animal_img.filename
        print(filename)
        animal_img.save(static_path+"uploads\\animal_img\\"+id+filename)
        print("kkkkk")
    print("mmmmmmmmmm")

    db.update("UPDATE animal_database SET name ='"+animal_name+"' ,description='"+animal_desc+"',photo='"+id+filename+"' WHERE animal_id="+ids+"")

    return "<script>alert('Data added successfully');window.location='/admin/add_animalD';</script>"



@app.route('/admin/viewUsers')
def viewUsers():
    loginId = session['lid']
    if auth_check(loginId) == 1:
        db = Db()
        result = db.select("SELECT * FROM user_data")
        return render_template('admin/view_users.html', data=result)
    else:
        return "Access denied"



#===============================================   ADMIN MODULE ends  ======================================

#===============================================   USER MODULE   ===========================================

@app.route('/animal_details')
def animal_details():
    loginId = session['lid']
    db = Db()
    result = db.select("SELECT * FROM animal_database")
    return render_template('animal_details.html', data=result)


@app.route('/feedback')
def feedback():
    return render_template('feedback.html')

@app.route('/feedbackFn', methods=['POST'])
def feedbackFn():
    feedback_ = request.form['feedback']
    loginId =str(session['lid'])

    if feedback_ == "":
        return "<script>alert('Oops!! Something went Wrong!!');window.location='/feedback';</script>"

    else:
        db = Db()
        db.insert(
            "INSERT INTO feedback_data(user_id,date,feedback) values ('" + loginId + "',now(),'" + feedback_ + "') ")
        flash("Feedback added")
        return redirect(url_for('feedback'))



@app.route('/register')
def register():
    return render_template('register.html')

@app.route('/registerFn',methods=['POST'])
def registerFn():
    username = request.form['username']
    email = request.form['email']
    phone = request.form['phone']
    location = request.form['location']
    password = request.form['password']
    confirm_password = request.form['confirm_password']

    if password == confirm_password:
        if location == '':
            location = "Not available"
        db = Db()
        lid=db.insert("INSERT INTO authentication(username,password,user_access) values ('" + email + "','" + password+ "','user') ")

        loginId = str(lid)

        db.insert("INSERT INTO user_data(login_id,name,place,phone,email) values ('"+loginId+"','" + username + "','" + location + "','"+phone+"','"+email+"')")
        flash("Please Login to continue")
        return "<script>alert('Registration sucessful! Please log in to continue.');window.location='/login';</script>"


    else :
        flash("Oops! Passwords doesn't match.")
        return redirect(url_for('register'))

@app.route('/forgotpassword')
def forgotPassword():
    return render_template('forgotpassword.html')

@app.route('/forgotpasswordFn',methods=['POST'])
def forgotPasswordFn():
    email = request.form['email']
    db = Db()
    query = db.select_one("Select * from authentication where username='"+email+"'")
    if query is not None:
        loginId = str(query['login_id']).encode()

        loginId = base64.b64encode(loginId)
        loginId = loginId.decode()
        key = "http://127.0.0.1:4000/resetpassword/"+loginId
        msg = Message(
            'Reset your password',
            sender='soundifyapp20@gmail.com',
            recipients=[email]
        )
        msg.body ="Here is your password reset link!\n\n"+key+"\n\nRegards,\nTeam Soundify"

        mail.send(msg)
        flash("If the email is registered you will recieve a password reset link shortly.")
        return redirect(url_for('forgotPassword'))
    flash("If the email is registered you will recieve a password reset link shortly.")
    return redirect(url_for('forgotPassword'))

@app.route('/resetpassword/<id>')
def resetPassword(id):
    resetId = id.encode()
    resetId = base64.b64decode(resetId)
    resetId = resetId.decode()
    session['resetId'] = resetId
    return render_template('resetpassword.html')

@app.route('/resetpasswordFn',methods=['POST'])
def resetPasswordFn():
    password = request.form['password']
    confirmpassword =request.form['confirmpassword']

    if(password == confirmpassword):
        db = Db()
        resetId = str(session['resetId'])
        db.update("UPDATE authentication SET password ='"+password+"'WHERE login_id="+resetId+"")
        session.pop('resetId', None)
        flash("Password reset successfully! Log in now")
        return redirect(url_for('login'))
    else:
        flash("passwords dosen't match! Please try again.")
        return redirect(url_for('forgotPassword'))

@app.route('/')
def index():
    #username = session['username']
    return render_template('index.html')


@app.route('/dashboard')
def dashboard():
    if session.get('lid'):
        return render_template('dashboard.html')

    return redirect(url_for('login'))

@app.route('/profile')
def profile():
    if session.get('email'):
        uid = str(session['uid'])
        db = Db()
        res = db.select_one("SELECT * FROM user_data WHERE user_id ='"+uid+"'")
        return render_template('profile.html', data=res)

    return redirect(url_for('login'))


@app.route('/editProfile')
def editProfile():
    if session.get('uid'):
        uid = str(session['uid'])
        db = Db()
        res = db.select_one("SELECT * FROM user_data WHERE user_id ='"+uid+"'")
        return render_template('editprofile.html', data=res)

    return redirect(url_for('login'))

@app.route('/updateProfile' , methods=['POST'])
def UpdateProfile():
    if session.get('email'):
        uid = str(session['uid'])
        name = request.form['name']
        place = request.form['place']
        phone = request.form['phone']
        email = request.form['email']
        photo = request.files['photo']


        db = Db()

        #fetchphoto = db.select_one("SELECT * FROM user_data WHERE user_id ='"+uid+"'")
        if photo.filename != '':

            filename = "user_" + uid + ".jpg"
            photo.save(static_path + "uploads\\user_image\\" + filename)
            db.update(
                "UPDATE user_data SET name ='" + name + "' ,place='" + place + "',phone ='" + phone + "' ,email ='" + email + "',photo ='" + filename + "' WHERE user_id=" + uid + "")
        else:
            db.update(
                "UPDATE user_data SET name ='" + name + "' ,place='" + place + "',phone ='" + phone + "' ,email ='" + email + "' WHERE user_id=" + uid + "")




        res = db.select_one("SELECT * FROM user_data WHERE user_id ='"+uid+"'")
        return render_template('profile.html', data=res)

    return redirect(url_for('login'))


@app.route('/history')
def history():
    if session.get('lid'):
        uid = str(session['uid'])
        print(uid)
        db = Db()
        res = db.select("SELECT * FROM prediction_results , animal_database WHERE prediction_results.animal_id = animal_database.animal_id AND prediction_results.user_id='"+uid+"' ORDER BY date DESC")

        return render_template('prediction_history.html' ,data=res)

    return redirect(url_for('login'))

@app.route('/sound_upload',methods=['POST'])
def sound_upload():
    uid = session['uid']
    animal_sound = request.files['soundData']
    extention = animal_sound.filename.split(".")[-1]
    db = Db()
    res = db.insert(
        "INSERT INTO prediction_results(user_id,date) values ('" + str(uid) + "',now())")
    filename = "uploads_" + str(res) + extention
    animal_sound.save(static_path + "uploads\\sound_data\\" + filename)

    db.update("UPDATE prediction_results SET file ='" + filename + "' WHERE prediction_id=" + str(res) + "")

    return "uploaded"
#===============================================   USER MODULE  ends  ======================================

#=============================================== authentication  ===========================================

@app.route('/login')
def login():
    return render_template('login.html')

@app.route('/login_check',methods=['POST'])
def loginCheck():
    email = request.form['email']
    password= request.form['password']
    db=Db()
    res = db.select_one("SELECT * FROM authentication WHERE username='"+email+"' AND password = '"+password+"'")


    if res is not None:
        user_access = res['user_access']
        session['lid'] = res['login_id']
        login_id =str(res['login_id'])
        session['email'] = res['username']

        loginId = str(res['login_id'])
        if loginId !="1":
            userIdQuery = db.select_one(
                "SELECT * FROM user_data WHERE login_id='" + login_id
                + "' ")

            session['uid']  = userIdQuery['user_id']
            res_2 = db.select_one(
                "SELECT * FROM user_data WHERE login_id='" + loginId + "'")
            session['uid'] = res_2['user_id']
            session['username'] = res_2['name']

        if user_access == 'admin':
            return  redirect(url_for('console'))
        elif user_access == 'user':
            return  redirect(url_for('dashboard'))
    else:
        return "<script>alert('invalid username or password');window.location='/login';</script>"


@app.route('/logout')
def logout():
   session.pop('lid',None)
   session.pop('username', None)
   session.pop('uid', None)
   session.pop('email', None)
   return redirect(url_for('dashboard'))

#=============================================== authentication  ends ===========================================





#================================================       ANDROID SECTION       ==========================================

@app.route('/api_history' , methods=['POST'])
def api_history():
    lid=request.form['lid']
    db=Db()
    k={}
    res = db.select(
        "SELECT * FROM prediction_results,animal_database WHERE prediction_results.animal_id = animal_database.animal_id AND prediction_results.user_id='" + lid + "'")
    if res is not None:
        k['status']="success"
        k["data"]=res
        return jsonify(status="success",data=res)
    else:
        k["status"]="no"
    return jsonify(status="no")



#=============================================== User Registration  ===========================================
@app.route('/api_registerFn',methods=['POST'])
def api_registerFn():
    username = request.form['username']
    email = request.form['email']
    phone = request.form['phone']
    location = request.form['location']
    password = request.form['password']

    print("ok2")
    pic = request.files['pic']
    print("ok3")
    db1=Db()
    mm=db1.select_one("select max(user_id) as maxid from user_data")
    usid=mm['maxid']
    filename="user_"+str(usid)+".jpg";


    k={}
    db = Db()
    lid=db.insert("INSERT INTO authentication(username,password,user_access) values ('" + email + "','" + password+ "','user') ")

    loginId = str(lid)

    uid=db.insert("INSERT INTO user_data(login_id,name,place,phone,email,photo) values ('"+loginId+"','" + username + "','" + location + "','"+phone+"','"+email+"','"+filename+"')")
    pic.save(user_pic_path +"user_"+str(usid)+".jpg")

    print(user_pic_path+"user_"+str(usid)+".jpg")
    k["status"]="success"
    return jsonify(status="success")

@app.route('/api_viewProfile' , methods=['POST'])
def api_viewProfile():
    lid=request.form['lid']
    db=Db()
    k={}
    res=db.select_one("select * from user_data where login_id='"+lid+"'")
    if res is not None:
        return jsonify(status="success",name=res["name"],place=res["place"],phone=res["phone"],email=res["email"],photo=res["photo"])

    else:
        k["status"]="no"
        return jsonify(status="no")

#=============================================== update profile  ===========================================
@app.route('/api_updateProfile' , methods=['POST'])
def api_UpdateProfile():

    uid = request.form['lid']
    name = request.form['username']
    place = request.form['location']
    phone = request.form['phone']
    email = request.form['email']
    photo = request.form['photo']
    print(photo)

    k={}
    db = Db()

        #fetchphoto = db.select_one("SELECT * FROM user_data WHERE user_id ='"+uid+"'")
    if photo != 'yes':

        import time, datetime
        from encodings.base64_codec import base64_decode
        import base64

        timestr = time.strftime("%Y%m%d-%H%M%S")
        print(timestr)
        a = base64.b64decode(photo)
        fh = open("static/uploads/user_image/" + timestr + ".jpg", "wb")
        path = timestr + ".jpg"
        fh.write(a)
        fh.close()
        db.update(
            "UPDATE user_data SET name ='" + name + "' ,place='" + place + "',phone ='" + phone + "' ,email ='" + email + "',photo ='" + path + "' WHERE login_id=" + uid + "")
    else:
        db.update(
            "UPDATE user_data SET name ='" + name + "' ,place='" + place + "',phone ='" + phone + "' ,email ='" + email + "' WHERE login_id=" + uid + "")




    res = db.select_one("SELECT * FROM user_data WHERE login_id ='"+uid+"'")
    k["status"]="success"
    k["result"] = res

    return jsonify(status="success")

#=============================================== Sound Upload WEB ===========================================
@app.route('/publicupload')
def publicload():
    return render_template('publicdashboard.html')

@app.route('/sound_upload_web_public',methods=['POST'])
def sound_upload_web_public():
    # try:

         # uid = request.form['lid']
        animal_sound = request.files['soundData']
        extention = animal_sound.filename.split(".")[-1]
        db = Db()
        filename = "uploads_" + str("aaaa") +"."+ extention
        animal_sound.save(static_path + "uploads\\sound_data\\" + filename)


        import librosa
        import numpy as np
        y, sr = librosa.load(static_path + "uploads\\sound_data\\" + filename, mono=True)
        chroma_stft = librosa.feature.chroma_stft(y=y, sr=sr)
        spec_cent = librosa.feature.spectral_centroid(y=y, sr=sr)

        S, phase = librosa.magphase(librosa.stft(y))

        spec_bw = librosa.feature.spectral_bandwidth(y=y, sr=sr)
        rolloff = librosa.feature.spectral_rolloff(y=y, sr=sr)
        zcr = librosa.feature.zero_crossing_rate(y)
        mfcc = librosa.feature.mfcc(y=y, sr=sr)

        toappend=[]
        toappend.append(np.mean(chroma_stft))
        toappend.append(np.mean(spec_cent))
        toappend.append(np.mean(spec_bw))
        toappend.append(np.mean(rolloff))
        toappend.append(np.mean(zcr))

        for e in mfcc:
            toappend.append( np.mean(e))

        aatest= np.array([toappend])
        import pandas as pd

        a=pd.read_csv('F:\\Animal_sound_detection_01-03-2023\\Dataset\\data.csv')

        attributes= a.values[:,0:25]

        labels=a.values[:,25]

        from sklearn.ensemble import RandomForestClassifier

        rnd=RandomForestClassifier()

        rnd.fit(attributes,labels)


        ####################################################

        rk=RandomForestClassifier()

        trainx,trainy,testx,testy= train_test_split(attributes,labels,test_size=0.2,random_state=0)


        rk.fit(trainx,testx)


        pred=rk.predict(trainy)

        from sklearn.metrics import  accuracy_score

        acc=accuracy_score(testy,pred)

        print(acc)











        ####################################################






        c=rnd.predict(np.array(aatest))
        results=c[0]

        print(results)

        k={}
        kin=db.select_one("select * from animal_database where name='"+results+"'")
        if kin is not None:
            animal_id=str(kin['animal_id'])
            # db.update("UPDATE prediction_results SET file ='" + filename + "',animal_id='"+animal_id+"' WHERE prediction_id=" + str(res) + "")
          #  k["status"] = "success"
            #k["data"]=kin
            return render_template('public_predication_result.html',data=kin,acc=acc)
        else:
            # k["status"] = "No data"
            return render_template('public_predication_result.html',data="null")
    # except Exception as e:
    #     # k["status"] = "No data"
    #     print(e)
    #
    #     print("aaaaaaaaaaaa",e)
    #     return  render_template('public_predication_result.html',data="null")



@app.route('/sound_upload_web',methods=['POST'])
def sound_upload_web():
    k = {}
    # try:
    uid = session['uid']
     # uid = request.form['lid']
    animal_sound = request.files['soundData']
    extention = animal_sound.filename.split(".")[-1]
    db = Db()
    res = db.insert(
        "INSERT INTO prediction_results(user_id,date) values ('" + str(uid) + "',now())")
    filename = "uploads_" + str(res) +"."+ extention
    animal_sound.save(static_path + "uploads\\sound_data\\" + filename)


    import librosa
    import numpy as np

    y, sr = librosa.load(static_path + "uploads\\sound_data\\" + filename, mono=True)
    chroma_stft = librosa.feature.chroma_stft(y=y, sr=sr)
    print(y)
    spec_cent = librosa.feature.spectral_centroid(y=y, sr=sr)

    S, phase = librosa.magphase(librosa.stft(y))

    spec_bw = librosa.feature.spectral_bandwidth(y=y, sr=sr)
    rolloff = librosa.feature.spectral_rolloff(y=y, sr=sr)
    zcr = librosa.feature.zero_crossing_rate(y)
    mfcc = librosa.feature.mfcc(y=y, sr=sr)
    print(mfcc)
    toappend=[]
    toappend.append(np.mean(chroma_stft))
    toappend.append(np.mean(spec_cent))
    toappend.append(np.mean(spec_bw))
    toappend.append(np.mean(rolloff))
    toappend.append(np.mean(zcr))

    for e in mfcc:
        toappend.append( np.mean(e))

    aatest= np.array([toappend])
    import pandas as pd

    a=pd.read_csv('F:\\Animal_sound_detection_01-03-2023\\Dataset\\data.csv')

    attributes= a.values[:,0:25]

    labels=a.values[:,25]

    from sklearn.ensemble import RandomForestClassifier

    rnd=RandomForestClassifier()

    rnd.fit(attributes,labels)


    ####################################################

    rk=RandomForestClassifier()

    trainx,trainy,testx,testy= train_test_split(attributes,labels,test_size=0.2,random_state=0)


    rk.fit(trainx,testx)

    pred=rk.predict(trainy)

    from sklearn.metrics import  accuracy_score

    acc=accuracy_score(testy,pred)

    print(acc)











    ####################################################






    c=rnd.predict(np.array(aatest))
    results=c[0]
    print(results)

    print(results)


    kin=db.select_one("select * from animal_database where name='"+results+"'")
    if kin is not None:
        animal_id=str(kin['animal_id'])
        db.update("UPDATE prediction_results SET file ='" + filename + "',animal_id='"+animal_id+"' WHERE prediction_id=" + str(res) + "")
      #  k["status"] = "success"
        #k["data"]=kin
        return render_template('predication_result.html',data=kin,acc=acc)
    else:
        k["status"] = "No data"
        return render_template('predication_result.html',data="null")


#=============================================== Sound Upload Android ===========================================

@app.route('/api_sound_upload',methods=['POST'])
def api_sound_upload():

    k = {}
    try:

       # uid = session['uid']
        uid = request.form['lid']
        animal_sound = request.files['file']
        extention = animal_sound.filename.split(".")[-1]
        db = Db()
        res = db.insert(
            "INSERT INTO prediction_results(user_id,date) values ('" + str(uid) + "',now())")
        filename = "uploads_" + str(res) +"."+ extention

        print(filename)
        animal_sound.save(static_path + "uploads\\sound_data\\" + filename)
        print("saved")

        import librosa
        import numpy as np
        print("yes")

        print(filename)

        y, sr = librosa.load(static_path + "uploads\\sound_data\\" + filename, mono=True)

        print("done")
        print(y)
        chroma_stft = librosa.feature.chroma_stft(y=y, sr=sr)
        spec_cent = librosa.feature.spectral_centroid(y=y, sr=sr)

        S, phase = librosa.magphase(librosa.stft(y))
        print(S)
        spec_bw = librosa.feature.spectral_bandwidth(y=y, sr=sr)
        rolloff = librosa.feature.spectral_rolloff(y=y, sr=sr)
        zcr = librosa.feature.zero_crossing_rate(y)
        mfcc = librosa.feature.mfcc(y=y, sr=sr)
        print(mfcc)
        toappend=[]
        toappend.append(np.mean(chroma_stft))
        toappend.append(np.mean(spec_cent))
        toappend.append(np.mean(spec_bw))
        toappend.append(np.mean(rolloff))
        toappend.append(np.mean(zcr))

        for e in mfcc:
            toappend.append( np.mean(e))

        aatest= np.array([toappend])
        print("yes")
        import pandas as pd

        a=pd.read_csv('F:\\Animal_sound_detection_01-03-2023\\Dataset\\data.csv')

        attributes= a.values[:,0:25]

        labels=a.values[:,25]

        from sklearn.ensemble import RandomForestClassifier

        rnd=RandomForestClassifier()

        rnd.fit(attributes,labels)


        c=rnd.predict(np.array(aatest))
        results=c[0]
        print(results)


        kin=db.select_one("select * from animal_database where name='"+results+"'")
        if kin is not None:
            animal_id=str(kin['animal_id'])
            db.update("UPDATE prediction_results SET file ='" + filename + "',animal_id='"+animal_id+"' WHERE prediction_id=" + str(res) + "")
            k["status"] = "success"
            k["data"]=kin
            return jsonify(status="success",data=kin)

        else:
            k["status"] = "No data"
            return jsonify(status="No data")
    except Exception as e:
        print(e,"abcd")
        k["status"] = "No data"
        return  jsonify(status="No data")


#=============================================== authentication  ===========================================

@app.route('/api_login_check',methods=['POST'])
def api_loginCheck():
    k={}
    email = request.form['email']
    password= request.form['password']
    db=Db()
    res = db.select_one("SELECT * FROM authentication WHERE username='"+email+"' AND password = '"+password+"'")


    if res is not None:
        user_access = res['user_access']
        session['lid'] = res['login_id']
        login_id =str(res['login_id'])
        session['email'] = res['username']


        loginId = str(res['login_id'])
        if loginId !="1":
            userIdQuery = db.select_one(
                "SELECT * FROM user_data WHERE login_id='" + login_id
                + "' ")

            session['uid']  = userIdQuery['user_id']
            res_2 = db.select_one(
                "SELECT * FROM user_data WHERE login_id='" + loginId + "'")
            session['uid'] = res_2['user_id']
            session['username'] = res_2['name']

        k["status"]="success"
        k["loginId"]=loginId
        print(k)
        return  jsonify(status="success",loginId=loginId)
    else:
        k["status"] = "failed"
        return jsonify(status="failed")



@app.route('/api_forgotpasswordFn',methods=['POST'])
def api_forgotPasswordFn():
    email = request.form['email']
    ip = request.form['ip']
    db = Db()
    k={}
    query = db.select_one("Select * from authentication where username='"+email+"'")
    if query is not None:
        loginId = str(query['login_id']).encode()

        loginId = base64.b64encode(loginId)
        loginId = loginId.decode()
        key = "http://"+ip+":4000/resetpassword/"+loginId
        msg = Message(
            'Reset your password',
            sender='soundifyapp20@gmail.com',
            recipients=[email]
        )
        msg.body ="Here is your password reset link!\n\n"+key+"\n\nRegards,\nTeam Soundify"

        mail.send(msg)
        #flash("If the email is registered you will recieve a password reset link shortly.")
        k["status"]="ok"
        status="ok"
    # flash("If the email is registered you will recieve a password reset link shortly.")
    else:
        k["status"]="no"
        status="no"
    return  jsonify(status=status)



@app.route('/api_feedbackFn', methods=['POST'])
def api_feedbackFn():
    feedback_ = request.form['feedback']
    loginId =  request.form['lid']
    k={}
    db = Db()
    db.insert("INSERT INTO feedback_data(user_id,date,feedback) values ('" + loginId + "',curdate(),'" + feedback_ + "') ")
    k["status"] = "success"
    return jsonify(status="success")




@app.route('/training')
def training():

    import librosa
    import pandas
    import numpy as np

    c=[]
    header = 'chroma_stft spectral_centroid spectral_bandwidth rolloff zero_crossing_rate'
    for i in range(1, 21):
        header += ' mfcc'+str(i)
    header += ' label'
    header = header.split()
    #print(header)

    file = open('D:\\sound\\data.csv', 'w', newline='')
    with file:
        writer = csv.writer(file)
        writer.writerow(header)

    genres = 'bat elephant hornbill junglefowl macaque myna peafowl pig squirrel toad cat  '.split()

    for g in genres:
        for filename in os.listdir("D:\\sound\\data_set\\"+g):
            songname = "D:\\sound\\data_set\\"+g+"\\"+filename

            aa=[]

            y, sr = librosa.load(songname, mono=True)
            chroma_stft = librosa.feature.chroma_stft(y=y, sr=sr)
            spec_cent = librosa.feature.spectral_centroid(y=y, sr=sr)

            S, phase = librosa.magphase(librosa.stft(y))



            spec_bw = librosa.feature.spectral_bandwidth(y=y, sr=sr)
            rolloff = librosa.feature.spectral_rolloff(y=y, sr=sr)
            zcr = librosa.feature.zero_crossing_rate(y)
            mfcc = librosa.feature.mfcc(y=y, sr=sr)
            to_append = str(np.mean(chroma_stft)) +" "+str(np.mean(spec_cent)) +" "+ str(np.mean(spec_bw)) +" "+str(np.mean(rolloff)) +" "+str(np.mean(zcr))

            aa.append(np.mean(chroma_stft))
            aa.append(np.mean(spec_cent))
            aa.append(np.mean(spec_bw))
            aa.append(np.mean(rolloff))
            aa.append(np.mean(zcr))



            for e in mfcc:
                to_append += " "+str(np.mean(e))
                aa.append(np.mean(e))

            to_append +=  " "+g
            aa.append(g)

            file = open('D:\\sound\\data.csv', 'a', newline='')
            with file:
                writer = csv.writer(file)
                writer.writerow(to_append.split())


            c.append(aa)











@app.route('/song_post')
def song_post():

    import librosa
    import numpy as np
    y, sr = librosa.load("D:\\sound\\data_set\\bat\\bat_1.wav", mono=True)
    chroma_stft = librosa.feature.chroma_stft(y=y, sr=sr)
    spec_cent = librosa.feature.spectral_centroid(y=y, sr=sr)

    S, phase = librosa.magphase(librosa.stft(y))

    spec_bw = librosa.feature.spectral_bandwidth(y=y, sr=sr)
    rolloff = librosa.feature.spectral_rolloff(y=y, sr=sr)
    zcr = librosa.feature.zero_crossing_rate(y)
    mfcc = librosa.feature.mfcc(y=y, sr=sr)

    toappend=[]
    toappend.append(np.mean(chroma_stft))
    toappend.append(np.mean(spec_cent))
    toappend.append(np.mean(spec_bw))
    toappend.append(np.mean(rolloff))
    toappend.append(np.mean(zcr))

    for e in mfcc:
        toappend.append( np.mean(e))

    aatest= np.array([toappend])
    import pandas as pd

    a=pd.read_csv('D:\\sound\\data.csv')

    attributes= a.values[:,0:25]

    labels=a.values[:,25]


    print("aaa",attributes)

    print("bbb",labels)


    from sklearn.ensemble import RandomForestClassifier

    rnd=RandomForestClassifier()

    rnd.fit(attributes,labels)


    c=rnd.predict(np.array(aatest))

    print("predicted",c)


    # db=Db()
    #
    # qry=" insert into song (song_id,user_id,song_file,status,created_at)values (null,'"+str( session["userid"])+"','"+sng.filename+"','"+str(c[0])+"',curdate())"
    # res=db.insert(qry)
    # print(res)
    #
    # return  render_template('/user/checksong.html',p=c,toappend=toappend)


    return "ok"






if __name__ == '__main__':
    app.run(host="0.0.0.0")

--------------insert users


--pw decrypted = admin
INSERT INTO user (user_name,email,first_name,last_name,password,role,enabled) VALUES('joel1','hjoel87@gmx.ch','Joel','Henz','$2a$12$JHYEx42z9u3j/Q/AoRMLI.wau3OCmahHhlQWcKf5JfObcKeQxHM7W','ADMIN',true);

--pw decrypted = user
INSERT INTO user (user_name,email,first_name,last_name,password,role,enabled) VALUES('joel2','henzjoel@gmail.com','Joel','Henz','$2a$12$e89TFk53jlBPscfL.YCTZ.8RukYnu66pcjLfaIA2prrtq1xbqcV.i','USER',true);


--------------insert categories
INSERT INTO category (category) VALUES('SKIN_AND_HAIR');
INSERT INTO category (category) VALUES('DIGESTION');
INSERT INTO category (category) VALUES('EARS_AND_EYES');
INSERT INTO category (category) VALUES('ACHES');
INSERT INTO category (category) VALUES('COLD_MEDICATION');
INSERT INTO category (category) VALUES('OTHER');

--------------insert products
---SKIN_AND_HAIR
INSERT INTO product (description,img_source,category_id,price) VALUES('Herpatch Serum 5ml','../img/products/bepanthen_cream_20g.PNG',1,15.66);
INSERT INTO product (description,img_source,category_id,price) VALUES('Bebanthen Pro Sensiderm Cream 20g','../img/products/bepanthen_cream_20g.PNG',1,10.38);

---DIGESTION
INSERT INTO product (description,img_source,category_id,price) VALUES('DulcoSoft Drink 250ml','../img/products/dulco_soft_drink_250ml.PNG',2,14.52);
INSERT INTO product (description,img_source,category_id,price) VALUES('uluxan 30 pieces','../img/products/uluxan 30 pc.PNG',2,12.78);

---EARS_AND_EYES
INSERT INTO product (description,img_source,category_id,price) VALUES('Otalgan Cerumen 10ml','../img/products/otalgan_cerumen_10ml.PNG',3,8.33);
INSERT INTO product (description,img_source,category_id,price) VALUES('Cerumenol 10ml','../img/products/cerumenol_10ml.PNG',3,8.33);

---ACHES
INSERT INTO product (description,img_source,category_id,price) VALUES('Perskindol Thermo Hot Gel 100ml','../img/products/perskindol_thermo_100ml.PNG',4,16.54);
INSERT INTO product (description,img_source,category_id,price) VALUES('Perskindol Massage Oil 250ml','../img/products/perskindo_massage_250ml.PNG',4,14.49);

---COLD_MEDICATION
INSERT INTO product (description,img_source,category_id,price) VALUES('Triomer Nasal Spray 245ml','../img/products/troimer_nasalspray_245ml.PNG',5,26.19);
INSERT INTO product (description,img_source,category_id,price) VALUES('ProSens Throat Spray 20ml','../img/products/prosens throat spray 20ml.PNG',5,15.49);

---OTHER
INSERT INTO product (description,img_source,category_id,price) VALUES('Berocca Boost 45 pieces','../img/products/berocca boost 45 pc.PNG',6,48.24);
INSERT INTO product (description,img_source,category_id,price) VALUES('Biane Enfant 150ml','../img/products/biane enfant 150ml.PNG',6,22.68);
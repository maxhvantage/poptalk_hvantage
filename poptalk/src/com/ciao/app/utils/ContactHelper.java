package com.ciao.app.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.ciao.app.datamodel.Contact;

/**
 * Created by rajat on 5/2/15.
 */
public class ContactHelper {
    String phone = null;
    String image_uri = "";


    public List<Contact> fetchContacts(Context context) {
        List<Contact> contactList = new ArrayList<Contact>();
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cur = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                image_uri = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Contact contact = new Contact(name,phone,image_uri);
                        contactList.add(contact);
                    }
                    pCur.close();
                    /*if (image_uri != null) {

                        Log.e("Name = ", "" + name + ", phone : " + phone);
                        Log.e("Image = ", "" + Uri.parse(image_uri));
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(image_uri));
                            Log.e("bitmap = ", "" + bitmap);

                        } catch (FileNotFoundException e) { // TODO Auto-generated catch block e.printStackTrace(); } catch (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); } }


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }*/
                }
            }
        }
        return contactList;
    }


}
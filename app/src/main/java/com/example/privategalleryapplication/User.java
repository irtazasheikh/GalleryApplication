package com.example.privategalleryapplication;

/**
 * Created by irtaz_000 on 7/9/2017.
 */
public class User {
    private int _id;
    private String _password;
    private String _parent_password;
    private String _type;

    public User(){

    }

    public User(String password,String type){
        this._password=password;
        this._type=type;
    }

    public int get_id(){
        return  _id;
    }

    public String get_type(){
        return  _type;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_password(){
        return this._password;
    }

    public String get_parent_password(){
        return this._parent_password;
    }

    public void set_password(String password){
        this._password=password;
    }

    public void set_password(String password,String password2){
        this._password=password;
        this._parent_password=password2;
    }
}

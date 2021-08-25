package com.waang.waang;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table IF NOT EXISTS graduate  ("
                + "_id integer primary key autoincrement,"
                + "전공 TEXT," + "공통교양 INTEGER," + "핵심교양 INTEGER," + "진로소양 INTEGER," + "교양합 INTEGER," + "핵심전공 INTEGER," + "심화전공 INTEGER," + "전공합 INTEGER," +"심화전공핵심 INTEGER," + "심화전공심화 INTEGER," + "심화전공합 INTEGER," +"전공총합 INTEGER," + "부전공 INTEGER," + "복수전공핵심 INTEGER," + "복수전공심화 INTEGER," + "복수전공합 INTEGER," + "교직 TINTEGER," + "총합 INTEGER);";

        String fs = "create table IF NOT EXISTS firstsemester  ("
                + "_id integer primary key autoincrement,"
                + "순번 TEXT, " + "개설학과전공 TEXT," + "학수번호 TEXT," + "교과목명 TEXT," + "분반 TEXT," + "이수구분 TEXT," + "영역 TEXT," + "학점 REAL," + "수강정원 INTEGER," +"시간표 TEXT," + "강의실 TEXT," + "담당교수 TEXT," +"캠퍼스 TEXT," + "수업유형 TEXT," + "평가등급유형 TEXT," + "수강안내사항 TEXT," + "대상자지정내용 TEXT);";



        String ss = "create table IF NOT EXISTS secondsemester  ("
                + "_id integer primary key autoincrement,"
                + "순번 TEXT, " + "개설학과전공 TEXT," + "학수번호 TEXT," + "교과목명 TEXT," + "분반 TEXT," + "이수구분 TEXT," + "영역 TEXT," + "학점 REAL," + "수강정원 INTEGER," +"시간표 TEXT," + "강의실 TEXT," + "담당교수 TEXT," +"캠퍼스 TEXT," + "수업유형 TEXT," + "평가등급유형 TEXT," + "수강안내사항 TEXT," + "대상자지정내용 TEXT);";

        String summer = "create table IF NOT EXISTS summersemester  ("
                + "_id integer primary key autoincrement,"
                + "순번 TEXT, " + "개설학과전공 TEXT," + "학수번호 TEXT," + "교과목명 TEXT," + "분반 TEXT," + "이수구분 TEXT," + "영역 TEXT," + "학점 REAL," + "수강정원 INTEGER," +"시간표 TEXT," + "강의실 TEXT," + "담당교수 TEXT," +"캠퍼스 TEXT," + "수업유형 TEXT," + "평가등급유형 TEXT," + "수강안내사항 TEXT," + "대상자지정내용 TEXT);";

        db.execSQL(fs);
        db.execSQL(ss);
        db.execSQL(summer);

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS graduate";
        String fs = "DROP TABLE IF EXISTS firstsemester";
        String ss = "DROP TABLE IF EXISTS secondsemester";
        String summer = "DROP TABLE IF EXISTS summersemester";


        db.execSQL(fs);
        db.execSQL(ss);
        db.execSQL(summer);
        db.execSQL(sql);
        onCreate(db);
    }


}

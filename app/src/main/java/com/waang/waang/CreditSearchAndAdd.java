package com.waang.waang;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class CreditSearchAndAdd extends AppCompatActivity {

    ListView classList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_search_and_add);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        classList = (ListView) findViewById(R.id.classList);
        /* 아이템 추가 및 어댑터 등록 */
        dataSetting();


    }
    private void dataSetting(){

        MyAdapter mMyAdapter = new MyAdapter();
        // 리스트 추가
        mMyAdapter.addItem("2021년 여름학기");
        mMyAdapter.addItem("2021년 2학기");
        mMyAdapter.addItem("2021년 1학기");


        /* 리스트뷰에 어댑터 등록 */
        classList.setAdapter(mMyAdapter);
        classList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a_parent, View a_view, int a_position, long a_id) {
                // 리스트뷰 클릭 함수 추가

                Bundle bundle = new Bundle();
                int index = mMyAdapter.getItem(a_position).getName().indexOf("년");
                int indexSem = mMyAdapter.getItem(a_position).getName().indexOf("학");
                String year = mMyAdapter.getItem(a_position).getName().substring(0, index);
                String semester = mMyAdapter.getItem(a_position).getName().substring(index+2, indexSem);
                bundle.putString("semesterStr", mMyAdapter.getItem(a_position).getName());
                bundle.putString("year", year);
                bundle.putString("semester", semester);

                if(a_position ==0) {
                    Intent intent = new Intent(getApplicationContext(), SummerSemester.class);
                    intent.putExtra("myBundle", bundle);
                    startActivity(intent);
                    finish();
                } else if(a_position ==1) {
                    Intent intent = new Intent(getApplicationContext(), SecondSemester.class);
                    intent.putExtra("myBundle", bundle);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(getApplicationContext(), FirstSemester.class);
                    intent.putExtra("myBundle", bundle);
                    startActivity(intent);
                    finish();
                }

                //Intent intent = new Intent();
                //startActivity(intent);
            }
        });
    }
}

class MyAdapter extends BaseAdapter {

    /* 아이템을 세트로 담기 위한 어레이 */
    private ArrayList<MyItem> mItems = new ArrayList<>();

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public MyItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Context context = parent.getContext();

        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_custom, parent, false);
        }

        /* 'listview_custom'에 정의된 위젯에 대한 참조 획득 */
        TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name) ;

        /* 각 리스트에 뿌려줄 아이템을 받아오는데 mMyItem 재활용 */
        MyItem myItem = getItem(position);

        /* 각 위젯에 세팅된 아이템을 뿌려준다 */
        tv_name.setText(myItem.getName());

        /* (위젯에 대한 이벤트리스너를 지정하고 싶다면 여기에 작성하면된다..)  */


        return convertView;
    }

    /* 아이템 데이터 추가를 위한 함수. 자신이 원하는대로 작성 */
    public void addItem(String name) {

        MyItem mItem = new MyItem();

        /* MyItem에 아이템을 setting한다. */
        mItem.setName(name);

        /* mItems에 MyItem을 추가한다. */
        mItems.add(mItem);

    }
}

class MyItem {

    private String name;
    private String contents;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
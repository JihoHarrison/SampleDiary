package com.example.samplediary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private TextView tv_name, tv_currentDate, tv_writeDate; // 출력할 텍스트뷰입니다.(이름, 현재 날짜, 일기장 날짜)
    private ImageView datepicker; // DatePicker 대화상자를 표시할 매개체입니다.
    private EditText edit_diary; // 일기 내용이 들어갈 에딧텍스트입니다.
    private Button btn_write, btn_delete; // 일기 저장(수정), 삭제 버튼입니다.
    private int cYear, cMonth, cDay; // 현재 날짜 정보를 저장할 변수입니다.(년, 월, 일)
    private String fileName; // 일기장 파일명을 저장할 변수입니다.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("일기장 만들기");

        // xml에서 불러와서 연결시켜줍니다.
        tv_name = findViewById(R.id.tv_name);
        tv_currentDate = findViewById(R.id.tv_currentDate);
        tv_writeDate = findViewById(R.id.tv_writeDate);
        datepicker = findViewById(R.id.hello);
        edit_diary = findViewById(R.id.edit_diary);
        btn_write = findViewById(R.id.btn_write);
        btn_delete = findViewById(R.id.btn_delete);

        // 당신의 이름을 적어주세요!
        tv_name.setText("신지호");

        // 현재 날짜 정보를 가져옵니다.
        Calendar calendar = Calendar.getInstance();
        cYear = calendar.get(Calendar.YEAR);
        cMonth = calendar.get(Calendar.MONTH); // Month값은 0부터 시작하므로 사용자에게 보여줄 때는 +1을 더해주어야합니다.
        cDay = calendar.get(Calendar.DAY_OF_MONTH);

        // 현재 날짜 정보를 텍스트뷰에 출력해줍니다.
        tv_currentDate.setText(cYear + "년 " + (cMonth+1) + "월 " + cDay + "일");

        // DatePicker 대화상자를 열어줄 이미지뷰를 클릭했을 때 이벤트 리스너입니다.
        datepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DatePicker 대화상자 객체를 생성합니다.
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        android.R.style.Theme_DeviceDefault_Dialog, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        // DatePicker 대화상자로 선택한 날짜를 텍스트뷰에 표시해줍니다.
                        tv_writeDate.setText(year + "년 " + (month+1) + "월 " + day + "일");
                        // month값은 0부터 시작하므로 사용자에게 보여줄 때는 +1을 더해주어야합니다.


                        // 선택한 날짜에 해당하는 일기장을 불러옵니다.
                        // 일기장 파일명을 저장합니다.
                        fileName = year + "_" + (month+1) + "_" + day + ".txt";
                        // month값은 0부터 시작하므로 사용자에게 보여줄 때는 +1을 더해주어야합니다.

                        edit_diary.setText(readDiary(fileName)); // 일기를 읽어서 에딧텍스트에 출력해줍니다.
                    }
                }, cYear, cMonth, cDay);
                datePickerDialog.show(); // DatePicker 대화상자를 보여줍니다.
            }
        });

        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fileName == null || fileName.length() < 1) {
                    // DatePicker 대화상자에서 날짜를 선택하지 않았다면(fileName이 없는 경우)
                    Toast.makeText(MainActivity.this, "날짜를 먼저 선택해주세요!", Toast.LENGTH_SHORT).show();
                } else {
                    // 선택한 날짜에 해당하는 일기장 파일을 저장합니다.
                    writeDiary(fileName);
                }
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fileName == null || fileName.length() < 1) {
                    // DatePicker 대화상자에서 날짜를 선택하지 않았다면(fileName이 없는 경우)
                    Toast.makeText(MainActivity.this, "날짜를 먼저 선택해주세요!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // 선택한 날짜에 해당하는 일기장 파일을 저장합니다.
                    deleteDiary(fileName);
                }
            }
        });
    }

    // 일기장 데이터를 읽어오기 위한 함수입니다.
    private String readDiary(String fileName) {
        String diaryData = null; // 일기 데이터가 담길 변수입니다.
        FileInputStream inputStream;
        try {
            inputStream = openFileInput(fileName);
            byte[] temp = new byte[500]; // 일기 데이터를 임시로 담을 Byte 배열 변수입니다.
            inputStream.read(temp); // 일기 데이터를 임시로 만든 temp 변수에 Byte 단위로 읽어와 저장합니다.
            inputStream.close();
            diaryData = (new String(temp)).trim(); // 읽어온 데이터를 문자열 타입으로 변환, 가공해줍니다.
            edit_diary.setText(""); // 일기 내용 에딧텍스트에 입력된 내용을 비워줍니다.
            edit_diary.setHint("일기장이 없습니다.");
            btn_write.setText("수정하기"); // 일기 데이터가 존재하니 글쓰기가 아니라 수정하기로 버튼을 표시해줍니다.
        } catch (IOException e) {
            edit_diary.setText(""); // 일기 내용 에딧텍스트에 입력된 내용을 비워줍니다.
            edit_diary.setHint("일기장이 없습니다.");
            btn_write.setText("작성하기"); // 일기 파일이 없으므로 일기 데이터 역시 존재하지 않기 때문에 글쓰기로 버튼을 표시해줍니다.
        }
        return diaryData; // 불러온 일기 내용을 반환합니다.
    }

    // 일기장 데이터를 저장하기 위한 함수입니다.
    private void writeDiary(String fileName) {
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            String diaryData = edit_diary.getText().toString(); // 에딧텍스트에 입력한 일기 내용을 가져옵니다.
            outputStream.write(diaryData.getBytes()); // 일기 내용을 Byte 단위로 변환하여 파일로 저장합니다.
            outputStream.close();
            Toast.makeText(getApplicationContext(), "성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
            edit_diary.setText(readDiary(fileName)); // 일기를 다시 불러옵니다.
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "저장에 실패했습니다!", Toast.LENGTH_SHORT).show();
        }
    }

    // 일기장 데이터 파일을 삭제하기 위한 함수입니다.
    private void deleteDiary(String fileName) {
        File diaryFile;
        try {
            // 삭제할 일기장 파일 경로입니다.
            String path = getCacheDir().getParent() + File.separator + "files" + File.separator + fileName;
            // 경로가 궁금한 경우 아래 코드의 주석을 풀고 삭제 기능을 사용하면 안드로이드 스튜디오 Logcat에서 경로를 출력시켜볼 수 있습니다.
            Log.i("[파일 경로 출력]", path);
            diaryFile = new File(path); // 삭제할 일기장 파일 객체입니다.
            if (diaryFile.exists() && diaryFile.isFile()) { // 해당 경로가 존재하는지, 삭제 가능한 파일인지 확인합니다.
                diaryFile.delete(); // 파일을 삭제합니다.
                Toast.makeText(getApplicationContext(), "성공적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "파일이 존재하지 않습니다!", Toast.LENGTH_SHORT).show();
            }
            edit_diary.setText(readDiary(fileName)); // 일기를 다시 불러옵니다.
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "삭제에 실패했습니다!", Toast.LENGTH_SHORT).show();
        }
    }
}
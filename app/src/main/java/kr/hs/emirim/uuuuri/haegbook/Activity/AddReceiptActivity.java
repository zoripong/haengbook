package kr.hs.emirim.uuuuri.haegbook.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import kr.hs.emirim.uuuuri.haegbook.R;

public class AddReceiptActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_receipt);

        initialize();
    }



    private void initialize() {
        findViewById(R.id.done_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddReceiptActivity.this, TravelDetailActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}

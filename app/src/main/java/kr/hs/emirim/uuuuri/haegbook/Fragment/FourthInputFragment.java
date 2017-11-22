package kr.hs.emirim.uuuuri.haegbook.Fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import kr.hs.emirim.uuuuri.haegbook.Interface.CurrencyTag;
import kr.hs.emirim.uuuuri.haegbook.Interface.SharedPreferenceTag;
import kr.hs.emirim.uuuuri.haegbook.Manager.SharedPreferenceManager;
import kr.hs.emirim.uuuuri.haegbook.R;

/**
 * Created by 유리 on 2017-11-04.
 */

public class FourthInputFragment extends Fragment{

    private EditText mBeforeMoneyEt;
    private EditText mAfterMoneyEt;

    private TextView mAfterMoneyTv;

    private Spinner mAfterCountrySp;
    private Spinner mKoreaSp;
    private boolean beforeFocus=true;
    private boolean afterFocus=false;

    private float beforeMoney=0;
    private float afterMoney=0;

    private String country;//여행갈 나라
    private boolean isKoreaTravel;
    private double[] rate;//환율

    private String[] spinnerCountry={"미국","유럽연합","일본","중국","홍콩","대만","영국","오만","캐나다","스위스","스웨덴","호주","뉴질랜드"
            ,"체코","칠레","터키","몽골","이스라엘","덴마크","노르웨이","사우디아라비아","쿠웨이트","바레인","아랍에미리트","요르단"
            ,"이집트","태국","싱가포르","말레이시아","인도네시아","카타르","카자하스탄","브루나이","인도","파키스탄","방글라데시","필리핀"
            ,"멕시코","브라질","베트남","남아프리카공화국","러시아","헝가리","폴란드"};
    int currencyIndex=0;
    private String[] currency={"$","€","¥","¥","$","$","£","ر.ع.","$","₣","kr","$","$","Kč","$","₤","₮",
            "₪","kr","kr","ر.س","د.ك","ب.د","د.إ","د.ا","£","฿","$","RM","Rp","ر.ق","〒","$","₨","₨","৳",
            "₱","$","R$","₫","R","р.","Ft","zł"};


    public FourthInputFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fourth_input, container, false);

        mAfterMoneyTv=(TextView) rootView.findViewById(R.id.after_money_tv);

        mBeforeMoneyEt=(EditText)rootView.findViewById(R.id.before_money_et);
        mBeforeMoneyEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                changeBeforeMoney();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mAfterMoneyTv.setText(currency[currencyIndex]);
            }
        });
        mBeforeMoneyEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean gainFocus) {
                if (gainFocus)
                    beforeFocus=true;
                else
                    beforeFocus = false;

            }
        });


        mAfterMoneyEt=(EditText)rootView.findViewById(R.id.after_money_et);
        mAfterMoneyEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                changeAfterMoney();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        mAfterMoneyEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean gainFocus) {
                if (gainFocus)  afterFocus=true;
                else    afterFocus=false;
            }
        });
        mKoreaSp = (Spinner)rootView.findViewById(R.id.korea_sp);
        mKoreaSp.setEnabled(false);
        mAfterCountrySp = (Spinner)rootView.findViewById(R.id.after_country_sp);

        mAfterCountrySp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                mAfterMoneyTv.setText(currency[position]);
                currencyIndex=position;
                changeBeforeMoney();
                changeAfterMoney();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        return rootView;
    }

    private void changeBeforeMoney(){
        if(beforeFocus) {
            try {
                beforeMoney = Float.parseFloat(mBeforeMoneyEt.getText().toString());
                if(!isKoreaTravel) {
                    afterMoney = (float) (Math.round(beforeMoney * rate[currencyIndex] * 1000) / 1000.0);
                    if (afterMoney <= 0)
                        mAfterMoneyEt.setText(String.valueOf(0));
                    else mAfterMoneyEt.setText(String.valueOf(afterMoney));
                }
            } catch (NumberFormatException e) {
                beforeMoney = 0;
                mAfterMoneyEt.setText(String.valueOf(0));
            }
        }

    }
    private void changeAfterMoney(){
        if (afterFocus) {
            try {
                afterMoney = Float.parseFloat(mAfterMoneyEt.getText().toString());
                beforeMoney = (float) (Math.round(afterMoney / rate[currencyIndex] * 1000) / 1000.0);
                if (beforeMoney <= 0)
                    mBeforeMoneyEt.setText(String.valueOf(0));
                else mBeforeMoneyEt.setText(String.valueOf(beforeMoney));
            } catch (NumberFormatException e) {
                afterMoney = 0;
                mBeforeMoneyEt.setText(String.valueOf(0));
            }
        }
    }

    public void getData(){
        if(getActivity() == null)
            return;
        SharedPreferenceManager spm = new SharedPreferenceManager(getActivity());
        Toast.makeText(getContext(), spm.retrieveString(SharedPreferenceTag.ADDRESS_TAG), Toast.LENGTH_SHORT).show();
        country = spm.retrieveString(SharedPreferenceTag.ADDRESS_TAG);

        for(int i=0;i<spinnerCountry.length;i++){
            if(country.equals(spinnerCountry[i])) {
                mAfterCountrySp.setSelection(i);
                break;
            }
        }
        Toast.makeText(getContext(), String.valueOf(spm.retrieveBoolean(SharedPreferenceTag.IS_KOR_TAG)), Toast.LENGTH_SHORT).show();
        isKoreaTravel=spm.retrieveBoolean(SharedPreferenceTag.IS_KOR_TAG);
        if(isGoingForeignTravel()){

            new getRate().execute(); //환율 가져오기

        }
    }




    public boolean saveData(){
        if(beforeMoney==0)
            return false;
        SharedPreferenceManager spm = new SharedPreferenceManager(getActivity());
        spm.save(SharedPreferenceTag.KOR_MONEY_TAG, beforeMoney);


        if(!isKoreaTravel) {
            spm.save(SharedPreferenceTag.FOREIGN_MONEY_TAG, afterMoney);
            spm.save(CurrencyTag.CURRENCY_COUNTRY_TAG, spinnerCountry[currencyIndex]);
            spm.save(CurrencyTag.CURRENCY_SYMBOL_TAG, currency[currencyIndex]);
            spm.save(SharedPreferenceTag.KOR_MONEY_TAG, beforeMoney);
        }

        return true;
    }


    private boolean isGoingForeignTravel(){//외국이면 리턴 투르
        if(isKoreaTravel) {
            mAfterMoneyTv.setVisibility(View.GONE);
            mAfterMoneyEt.setVisibility(View.GONE);
            mAfterCountrySp.setVisibility(View.GONE);
            return false;
        }
        return true;
    }

    private class getRate extends AsyncTask<Void,Void,Void> {
        private Elements titleElement;//나라이름
        private Elements saleElement;//환율
        ProgressDialog gettingRateDialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {

            gettingRateDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            gettingRateDialog.setMessage("로딩중입니다..");
            gettingRateDialog.setCancelable(false);
            gettingRateDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            gettingRateDialog.dismiss();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document doc = Jsoup.connect("http://info.finance.naver.com/marketindex/exchangeList.nhn").get();
                titleElement = doc.select(".tit");
                saleElement = doc.select(".sale");
                rate=new double[titleElement.size()];
                for(int i=0;i<titleElement.size();i++){
                    Log.e("기호",currency[i]);
                    String parsingCountry=titleElement.get(i).text().replaceAll("[^\uAC00-\uD7AF\u1100-\u11FF\u3130-\u318F]", "");
                    parsingCountry=parsingCountry.replaceAll("엔","");
                    double sale=Double.parseDouble(saleElement.get(i).text().replaceAll(",",""));
                    Log.e("선택한 국가",parsingCountry);
                    Log.e("환율",String.valueOf(sale));
                    rate[i]=Math.round(1/sale * 100000000) / 100000000.0;
                    if(country.contains("일본")){
                        rate[i]=Math.round(1/(sale/100) * 100000000) / 100000000.0;
                    }
                    if(country.contains(parsingCountry)){
                        currencyIndex=i;
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}

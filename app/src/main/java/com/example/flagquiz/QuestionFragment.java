package com.example.flagquiz;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.flagquiz.databinding.FragmentQuestionBinding;
import com.example.flagquiz.databinding.FragmentSigninBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class QuestionFragment extends Fragment {
    private OnAnswerSelectedListener answerSelectedListener;

    private FragmentQuestionBinding binding;
    ArrayList<Country> countries;
    boolean ca;
    private int score=0;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore firestore;
    Country country,falseCountry1,falseCountry2,falseCountry3;

    public QuestionFragment() {
        // Required empty public constructor
    }


    public static QuestionFragment newInstance(int score) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putInt("score", fragment.score);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        countries = new ArrayList<>();
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        firestore=FirebaseFirestore.getInstance();

    }

    private void getCorrectImage(int correctAnswer) {
        if (correctAnswer==1){
            binding.image1.setImageResource(country.getFlag());

        } else if (correctAnswer==2) {
            binding.image2.setImageResource(country.getFlag());

        } else if (correctAnswer==3) {
            binding.image3.setImageResource(country.getFlag());

        }else {
            binding.image4.setImageResource(country.getFlag());

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentQuestionBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




        getCountries();
        if (getArguments() != null) {
            score = getArguments().getInt("score", 0);
        }

        Random random = new Random();
        int r = random.nextInt(countries.size());

        country = countries.get(r);

        binding.countryNameText.setText(country.getCountryName());
        int correctAnswer = random.nextInt(4) + 1;

        // Doğru yanıt
        getCorrectImage(correctAnswer);

        // Yanlış cevaplar
        int falseAnswer1, falseAnswer2, falseAnswer3;

        // Yanlış cevapları farklı seçene kadar tekrar seç
        do {
            falseAnswer1 = random.nextInt(countries.size());
        } while (falseAnswer1 == r);

        do {
            falseAnswer2 = random.nextInt(countries.size());
        } while (falseAnswer2 == r || falseAnswer2 == falseAnswer1);

        do {
            falseAnswer3 = random.nextInt(countries.size());
        } while (falseAnswer3 == r || falseAnswer3 == falseAnswer1 || falseAnswer3 == falseAnswer2);

        falseCountry1 = countries.get(falseAnswer1);
        falseCountry2 = countries.get(falseAnswer2);
        falseCountry3 = countries.get(falseAnswer3);

        getFalseAnswarImage(correctAnswer,falseCountry1,falseCountry2,falseCountry3);


        image1Clicked(view,correctAnswer);
        image2Clicked(view,correctAnswer);
        image3Clicked(view,correctAnswer);
        image4Clicked(view,correctAnswer);


    }
    private void falseAnswerFirebaseUpdate(){
        if (user != null) {
            firestore.collection("Users").whereEqualTo("email", user.getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                    if (value != null) {
                        for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                            Map<String, Object> data = documentSnapshot.getData();
                                String sscore = (String) data.get("score");
                                int nscore=Integer.parseInt(sscore);
                                if (score>nscore){
                                    //yeni score'u kaydet
                                    updateFirebase();
                                }
                        }
                    }
                }
            });
        }
    }

    private void updateFirebase() {
        DocumentReference reference=firestore.collection("Users").document(user.getUid());
        reference.update("score",score).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getContext(),"Skor Güncellendi",Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void newFragment(){
        String dataToSend = "DogruCevap";
        ((OnDataPassedListener) getActivity()).onDataPassed(dataToSend,score);

    }

    private void image1Clicked(View view,int correctAnswer) {
        binding.image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (correctAnswer==1){
                    correctAnswerTransactions();
                    answerSelectedListener.onAnswerSelected(true);

                }
                else{
                    binding.textView6.setText("Yanlış Cevap");
                    binding.textView6.setTextColor(getResources().getColor(R.color.Kırmızı));
                    alldisabled();
                    showAlertDialog();
                    falseAnswerFirebaseUpdate();
                }
            }
        });
    }
    private void image2Clicked(View view,int correctAnswer) {
        binding.image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (correctAnswer==2){
                    correctAnswerTransactions();
                    answerSelectedListener.onAnswerSelected(true);

                }
                else{
                    binding.textView6.setText("Yanlış Cevap");
                    binding.textView6.setTextColor(getResources().getColor(R.color.Kırmızı));
                    alldisabled();
                    showAlertDialog();
                    falseAnswerFirebaseUpdate();

                }
            }
        });
    }
    private void correctAnswerTransactions(){
        score++;
        binding.textView6.setText("Doğru Cevap");
        binding.textView6.setTextColor(getResources().getColor(R.color.Yeşil));
        newFragment();
    }
    private void image3Clicked(View view,int correctAnswer) {
        binding.image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (correctAnswer==3){
                    correctAnswerTransactions();
                    answerSelectedListener.onAnswerSelected(true);

                }
                else{
                    binding.textView6.setText("Yanlış Cevap");
                    binding.textView6.setTextColor(getResources().getColor(R.color.Kırmızı));
                    alldisabled();
                    showAlertDialog();
                    falseAnswerFirebaseUpdate();

                }
            }
        });
    }
    private void image4Clicked(View view,int correctAnswer) {
        binding.image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (correctAnswer==4){
                    correctAnswerTransactions();
                    answerSelectedListener.onAnswerSelected(true);

                }
                else{
                    binding.textView6.setText("Yanlış Cevap");
                    binding.textView6.setTextColor(getResources().getColor(R.color.Kırmızı));
                    alldisabled();
                    showAlertDialog();
                    falseAnswerFirebaseUpdate();

                }
            }
        });
    }
    public void alldisabled() {
        binding.image1.setEnabled(false);
        binding.image2.setEnabled(false);
        binding.image3.setEnabled(false);
        binding.image4.setEnabled(false);
        binding.buttonGameSkip.setEnabled(false);

    }

    private void showAlertDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle("Oyun Bitti");
        builder.setMessage("Yanlış cevap verdiniz");
        builder.setCancelable(false);
        builder.setPositiveButton("Kapat", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                falseAnswerFirebaseUpdate();

                Intent intent=new Intent(getContext(), MainActivity2.class);
                startActivity(intent);

            }
        });
        builder.show();
    }


    private void getFalseAnswarImage(int correctAnswer,Country falseCountry1,Country falseCountry2,Country falseCountry3) {
        if (correctAnswer==1){
            binding.image2.setImageResource(falseCountry1.getFlag());
            binding.image3.setImageResource(falseCountry2.getFlag());
            binding.image4.setImageResource(falseCountry3.getFlag());
        } else if (correctAnswer==2) {
            binding.image1.setImageResource(falseCountry1.getFlag());
            binding.image3.setImageResource(falseCountry2.getFlag());
            binding.image4.setImageResource(falseCountry3.getFlag());
        } else if (correctAnswer==3) {
            binding.image1.setImageResource(falseCountry1.getFlag());
            binding.image2.setImageResource(falseCountry2.getFlag());
            binding.image4.setImageResource(falseCountry3.getFlag());
        }else {
            binding.image1.setImageResource(falseCountry1.getFlag());
            binding.image2.setImageResource(falseCountry2.getFlag());
            binding.image3.setImageResource(falseCountry3.getFlag());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            answerSelectedListener = (OnAnswerSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " OnAnswerSelectedListener arayüzünü uygulamalıdır");
        }
    }
    private void getCountries() {
        countries.add(new Country("AFGANİSTAN",R.drawable.afganistan));
        countries.add(new Country("ALMANYA",R.drawable.almanya));
        countries.add(new Country("AMERİKA BİRLEŞİK DEVLETLERİ",R.drawable.abd));
        countries.add(new Country("ANDORRA",R.drawable.andorra));
        countries.add(new Country("ANGOLA",R.drawable.angola));
        countries.add(new Country("ARNAVUTLUK",R.drawable.arnavutluk));
        countries.add(new Country("ANTİGUA VE BARBUDA",R.drawable.antiguavebarbuda));
        countries.add(new Country("ARJANTİN",R.drawable.arjantin));
        countries.add(new Country("AVUSTRALYA",R.drawable.avustralya));
        countries.add(new Country("AVUSTURYA",R.drawable.avusturya));
        countries.add(new Country("AZERBAYCAN",R.drawable.azerbaycan));

        countries.add(new Country("BAHAMALAR",R.drawable.bahamalar));
        countries.add(new Country("BAHREYN",R.drawable.bahreyn));
        countries.add(new Country("BANGLADEŞ",R.drawable.banglades));
        countries.add(new Country("BARBADOS",R.drawable.barbados));
        countries.add(new Country("BELÇİKA",R.drawable.belcika));
        countries.add(new Country("BELİZE",R.drawable.belize));
        countries.add(new Country("BENİN",R.drawable.benin));
        countries.add(new Country("BELARUS",R.drawable.belarus));
        countries.add(new Country("BHUTAN",R.drawable.bhutan));
        countries.add(new Country("BİRLEŞİK ARAP EMİRLİKLERİ",R.drawable.bae));
        countries.add(new Country("BOLİVYA",R.drawable.bolivya));
        countries.add(new Country("BİRLEŞİK KRALLIK",R.drawable.birlesikkrallik));
        countries.add(new Country("BOSNA-HERSEK",R.drawable.bosna));
        countries.add(new Country("BOTSVANA",R.drawable.botsvana));
        countries.add(new Country("BREZİLYA",R.drawable.brezilya));
        countries.add(new Country("BRUNEİ",R.drawable.brunei));
        countries.add(new Country("BULGARİSTAN",R.drawable.bulgaristan));
        countries.add(new Country("BURKİNA FASO",R.drawable.burkinafaso));
        countries.add(new Country("BURUNDİ",R.drawable.burundi));

        countries.add(new Country("CEZAYİR",R.drawable.cezayir));
        countries.add(new Country("ÇEKYA",R.drawable.cekya));
        countries.add(new Country("ÇİN",R.drawable.cin));

        countries.add(new Country("DANİMARKA",R.drawable.danimarka));
        countries.add(new Country("DOĞU TİMOR",R.drawable.dogutimor));
        countries.add(new Country("DOMİNİKA",R.drawable.dominika));
        countries.add(new Country("DOMİNİK CUMHURİYETİ",R.drawable.dominikcumhuriyeti));

        countries.add(new Country("EKVADOR",R.drawable.ekvador));
        countries.add(new Country("EKVATOR GİNESİ",R.drawable.ekvatorginesi));
        countries.add(new Country("EL SALVADOR",R.drawable.elsavador));
        countries.add(new Country("ENDONEZYA",R.drawable.endonezya));
        countries.add(new Country("ERİTRE",R.drawable.eritre));
        countries.add(new Country("ERMENİSTAN",R.drawable.ermenistan));
        countries.add(new Country("ESTONYA",R.drawable.estonya));
        countries.add(new Country("ESVATİNİ",R.drawable.esvatini));
        countries.add(new Country("ETİYOPYA",R.drawable.etiyopya));

        countries.add(new Country("FAS",R.drawable.fas));
        countries.add(new Country("FİJİ",R.drawable.fiji));
        countries.add(new Country("FİLDİŞİ SAHİLİ",R.drawable.fildisisahili));
        countries.add(new Country("FİLİPİNLER",R.drawable.filipinler));
        countries.add(new Country("FİLİSTİN",R.drawable.filistin));
        countries.add(new Country("FİNLANDİYA",R.drawable.finlandiya));
        countries.add(new Country("FRANSA",R.drawable.fransa));

        countries.add(new Country("GABON",R.drawable.gabon));
        countries.add(new Country("GAMBİYA",R.drawable.gambiya));
        countries.add(new Country("GANA",R.drawable.gana));
        countries.add(new Country("GİNE",R.drawable.gine));
        countries.add(new Country("GİNE-BİSSAU",R.drawable.ginebissau));
        countries.add(new Country("GRENADA",R.drawable.grenada));
        countries.add(new Country("GUATEMALA",R.drawable.guatemala));
        countries.add(new Country("GUYANA",R.drawable.guyana));
        countries.add(new Country("GÜNEY AFRİKA CUMHURİYETİ",R.drawable.guneyafrikacumhuriyeti));
        countries.add(new Country("GÜNEY KORE",R.drawable.guneykore));
        countries.add(new Country("GÜNEY SUDAN",R.drawable.guneysudan));
        countries.add(new Country("GÜRCİSTAN",R.drawable.gurcistan));

        countries.add(new Country("HAİTİ",R.drawable.haiti));
        countries.add(new Country("HIRVATİSTAN",R.drawable.hirvatistan));
        countries.add(new Country("HİNDİSTAN",R.drawable.hindistan));
        countries.add(new Country("HOLLANDA",R.drawable.hollanda));
        countries.add(new Country("HONDURAS",R.drawable.honduras));

        countries.add(new Country("IRAK",R.drawable.irak));

        countries.add(new Country("İRAN",R.drawable.iran));
        countries.add(new Country("İRLANDA",R.drawable.irlanda));
        countries.add(new Country("İSPANYA",R.drawable.ispanya));
        countries.add(new Country("İSRAİL",R.drawable.israil));
        countries.add(new Country("İSVEÇ",R.drawable.isvec));
        countries.add(new Country("İSVİÇRE",R.drawable.isvicre));
        countries.add(new Country("İTALYA",R.drawable.italya));
        countries.add(new Country("İZLANDA",R.drawable.izlanda));

        countries.add(new Country("JAMAİKA",R.drawable.jamaika));
        countries.add(new Country("JAPONYA",R.drawable.japonya));

        countries.add(new Country("KAMBOÇYA",R.drawable.kambocya));
        countries.add(new Country("KAMERUN",R.drawable.kamerun));
        countries.add(new Country("KANADA",R.drawable.kanada));
        countries.add(new Country("KARADAĞ",R.drawable.karadag));
        countries.add(new Country("KATAR",R.drawable.katar));
        countries.add(new Country("KAZAKİSTAN",R.drawable.kazakistan));
        countries.add(new Country("KENYA",R.drawable.kenya));
        countries.add(new Country("KIBRIS CUMHURİYETİ",R.drawable.kibris));
        countries.add(new Country("KIRGIZİSTAN",R.drawable.kirgisiztan));
        countries.add(new Country("KİRİBATİ",R.drawable.kribati));
        countries.add(new Country("KOLOMBİYA",R.drawable.kolombiya));
        countries.add(new Country("KOMORLAR",R.drawable.komorlar));
        countries.add(new Country("KONGO CUMHURİYETİ",R.drawable.kongo));
        countries.add(new Country("KONGO DC",R.drawable.kongo));
        countries.add(new Country("KOSTA RİKA",R.drawable.kostarika));
        countries.add(new Country("KOSOVA",R.drawable.kosova));
        countries.add(new Country("KUVEYT",R.drawable.kuveyt));
        countries.add(new Country("KUZEY KIBRIS TÜRK CUMHURİYETİ",R.drawable.kktc));
        countries.add(new Country("KUZEY KORE",R.drawable.kuzeykore));
        countries.add(new Country("KUZEY MAKEDONYA",R.drawable.kuzeymakedonya));
        countries.add(new Country("KÜBA",R.drawable.kuba));

        countries.add(new Country("LAOS",R.drawable.laos));
        countries.add(new Country("LESOTHO",R.drawable.lesotho));
        countries.add(new Country("LETONYA",R.drawable.letonya));
        countries.add(new Country("LİBERYA",R.drawable.liberya));
        countries.add(new Country("LİBYA",R.drawable.libya));
        countries.add(new Country("LİHTENŞTAYN",R.drawable.lihtenstayn));
        countries.add(new Country("LİTVANYA",R.drawable.litvanya));
        countries.add(new Country("LÜBNAN",R.drawable.lubnan));
        countries.add(new Country("LÜKSEMBURG",R.drawable.luksemburg));

        countries.add(new Country("MACARİSTAN",R.drawable.macaristan));
        countries.add(new Country("MADAGASKAR",R.drawable.madagaskar));
        countries.add(new Country("MALAVİ",R.drawable.malavi));
        countries.add(new Country("MALDİVLER",R.drawable.maldivler));
        countries.add(new Country("MALEZYA",R.drawable.malezya));
        countries.add(new Country("MALİ",R.drawable.mali));
        countries.add(new Country("MALTA",R.drawable.malta));
        countries.add(new Country("MARSHALL ADALARI",R.drawable.marshalladalari));
        countries.add(new Country("MAURİTİUS",R.drawable.mauritius));
        countries.add(new Country("MEKSİKA",R.drawable.meksika));
        countries.add(new Country("MİKRONEZYA FEDERAL DEVLETLERİ",R.drawable.mikronezya));
        countries.add(new Country("MISIR",R.drawable.misir));
        countries.add(new Country("MOĞOLİSTAN",R.drawable.mogolistan));
        countries.add(new Country("MOLDOVA",R.drawable.moldova));
        countries.add(new Country("MONAKO",R.drawable.monako));
        countries.add(new Country("MORİTANYA",R.drawable.moritanya));
        countries.add(new Country("MOZAMBİK",R.drawable.mozambik));
        countries.add(new Country("MYANMAR",R.drawable.myanmar));

        countries.add(new Country("NAMİBYA",R.drawable.namibya));
        countries.add(new Country("NAURU",R.drawable.nauru));
        countries.add(new Country("NEPAL",R.drawable.nepal));
        countries.add(new Country("NİJER",R.drawable.nijer));
        countries.add(new Country("NİJERYA",R.drawable.nijerya));
        countries.add(new Country("NİKARAGUA",R.drawable.nikaragua));
        countries.add(new Country("NİUE",R.drawable.niue));
        countries.add(new Country("NORVEÇ",R.drawable.norvec));

        countries.add(new Country("ORTA AFRİKA CUMHURİYETİ",R.drawable.ortaafrikacumhuriyeti));

        countries.add(new Country("ÖZBEKİSTAN",R.drawable.ozbekistan));

        countries.add(new Country("PAKİSTAN",R.drawable.pakistan));
        countries.add(new Country("PALAU",R.drawable.palau));
        countries.add(new Country("PANAMA",R.drawable.panama));
        countries.add(new Country("PAPUA YENİ GİNE",R.drawable.papuayenigine));
        countries.add(new Country("PARAGUAY",R.drawable.paraguay));
        countries.add(new Country("POLONYA",R.drawable.polonya));
        countries.add(new Country("PORTEKİZ",R.drawable.portekiz));

        countries.add(new Country("ROMANYA",R.drawable.romanya));
        countries.add(new Country("RUANDA",R.drawable.ruanda));
        countries.add(new Country("RUSYA",R.drawable.rusya));

        countries.add(new Country("SAİNT KİTTS VE NEVİS",R.drawable.saintkittsvenevis));
        countries.add(new Country("SAİNT LUCİA",R.drawable.saintlucia));
        countries.add(new Country("SAİNT VİNCENT VE GRENADİNLER",R.drawable.saintvicentvegrenadinler));
        countries.add(new Country("SAHRA DEMOKRATİK ARAP CUMHURİYETİ",R.drawable.sahrademokratarapcumhuriyeti));
        countries.add(new Country("SAMOA",R.drawable.samoa));
        countries.add(new Country("SAN MARİNO",R.drawable.sanmarino));
        countries.add(new Country("SAO TOME VE PRİNCİPE",R.drawable.saotomeveprincipe));
        countries.add(new Country("SENEGAL",R.drawable.senegal));
        countries.add(new Country("SEYŞELLER",R.drawable.seyseller));
        countries.add(new Country("SIRBİSTAN",R.drawable.sirbistan));
        countries.add(new Country("SİERRA LEONE",R.drawable.sierraleone));
        countries.add(new Country("SİNGAPUR",R.drawable.singapur));
        countries.add(new Country("SLOVAKYA",R.drawable.slovakya));
        countries.add(new Country("SLOVENYA",R.drawable.slovenya));
        countries.add(new Country("SOLOMON ADALARI",R.drawable.solomonadalari));
        countries.add(new Country("SOMALİ",R.drawable.somali));
        countries.add(new Country("SRİ LANKA",R.drawable.srilanka));
        countries.add(new Country("SUDAN",R.drawable.sudan));
        countries.add(new Country("SURİNAM",R.drawable.surinam));
        countries.add(new Country("SURİYE",R.drawable.suriye));
        countries.add(new Country("SUUDİ ARABİSTAN",R.drawable.suudiarabistan));

        countries.add(new Country("ŞİLİ",R.drawable.sili));

        countries.add(new Country("TACİKİSTAN",R.drawable.tacikistan));
        countries.add(new Country("TANZANYA",R.drawable.tanzanya));
        countries.add(new Country("TAYLAND",R.drawable.tayland));
        countries.add(new Country("TAYVAN",R.drawable.tayvan));
        countries.add(new Country("TOGO",R.drawable.togo));
        countries.add(new Country("TONGA",R.drawable.tonga));
        countries.add(new Country("TRİNİDAD VE TOBAGO",R.drawable.trinidadtobago));
        countries.add(new Country("TUNUS",R.drawable.tunus));
        countries.add(new Country("TUVALU",R.drawable.tuvalu));
        countries.add(new Country("TÜRKİYE",R.drawable.turkiye));
        countries.add(new Country("TÜRKMENİSTAN",R.drawable.turkmenistan));

        countries.add(new Country("UGANDA",R.drawable.uganda));
        countries.add(new Country("UKRAYNA",R.drawable.ukrayna));
        countries.add(new Country("UMMAN",R.drawable.umman));
        countries.add(new Country("URUGUAY",R.drawable.uruguay));

        countries.add(new Country("ÜRDÜN",R.drawable.urdun));

        countries.add(new Country("VANUATU",R.drawable.vanuatu));
        countries.add(new Country("VATİKAN",R.drawable.vatikan));
        countries.add(new Country("VENEZUELA",R.drawable.venezuela));
        countries.add(new Country("VİETNAM",R.drawable.vietnam));

        countries.add(new Country("YEŞİL BURUN ADALARI",R.drawable.yesilburunadalari));
        countries.add(new Country("YEMEN",R.drawable.yemen));
        countries.add(new Country("YENİ ZELANDA",R.drawable.yenizelanda));
        countries.add(new Country("YUNANİSTAN",R.drawable.yunanistan));

        countries.add(new Country("ZAMBİYA",R.drawable.zambiya));
        countries.add(new Country("ZİMBABVE",R.drawable.zimbabve));

    }

}
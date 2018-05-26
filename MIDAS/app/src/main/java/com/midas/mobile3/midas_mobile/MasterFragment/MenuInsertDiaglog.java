package com.midas.mobile3.midas_mobile.MasterFragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.midas.mobile3.midas_mobile.Model.Drink;
import com.midas.mobile3.midas_mobile.R;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MenuInsertDiaglog extends DialogFragment {
    private int GALLERY_CODE = 11;
    private String pathdata = "";
    private String key;

    private TextView cancel;
    private ImageView img;
    private Button imagebutton;
    private EditText name;
    private EditText cost;
    private EditText kcal;
    private Button insert;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private DatabaseReference mDatabase;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
            View root = inflater.inflate(R.layout.insert_fragment, container, false);
            Bundle mArgs = getArguments();
            final String status = mArgs.getString("Status");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        /*권한*/
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

            cancel = root.findViewById(R.id.cancel);
            img = root.findViewById(R.id.imageView2);
            imagebutton = root.findViewById(R.id.imagebutton);
            name = root.findViewById(R.id.editname);
            cost = root.findViewById(R.id.editcost);
            kcal = root.findViewById(R.id.editkcal);
            insert = root.findViewById(R.id.insert);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            imagebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityForResult(intent, GALLERY_CODE);
                }
            });

            if(status.equals("insert")){// 삽입

            }
            else if(status.equals("motify")){ //수정
                insert.setText("메뉴 수정");
                pathdata = mArgs.getString("Imageurl");
                key = mArgs.getString("key");
                Glide.with(root.getContext())
                        .load(pathdata)
                        .into(img);
                name.setText(mArgs.getString("name"));
                cost.setText(mArgs.getString("cost"));
                kcal.setText(mArgs.getString("kcal"));
            }

            insert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(status.equals("insert")){// 삽입
                        if(pathdata.equals("") == true || name.getText().toString().equals("") == true || cost.getText().toString().equals("") == true || kcal.getText().toString().equals("") == true){

                        }
                        else
                            Uploaddata(pathdata);
                    }
                    else if(status.equals("motify")) { //수정
                        if(pathdata.equals("") == true || name.getText().toString().equals("") == true || cost.getText().toString().equals("") == true || kcal.getText().toString().equals("") == true){

                        }
                        else{
                            motify();
                            dismiss();
                        }
                    }
                }
            });

            return root;
        }


    public Dialog onCreateDialog(Bundle savedInstanceState){
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.insert_fragment);
        return dialog;
    }

    public void show(FragmentManager fm, String few) {
        super.show(fm, few);
    }

    public String getPath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(getActivity(), uri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(index);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE) {
            File f= new File(getPath(data.getData()));
            img.setImageURI(Uri.fromFile(f));
            pathdata = getPath(data.getData());
        }
    }

    private void Uploaddata(String uri){
        final Uri file = Uri.fromFile(new File(uri));
        StorageReference riversRef = storageRef.child("images/" + file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                System.out.println(taskSnapshot.getDownloadUrl().toString());
                System.out.println(name.getText().toString()+"    "+cost.getText().toString()+"    "+kcal.getText().toString());
                writeNewMenu(taskSnapshot.getDownloadUrl().toString(), name.getText().toString(),cost.getText().toString(), kcal.getText().toString());
                System.out.println(taskSnapshot.getDownloadUrl().toString()); // 주소를 가져온다
            }
        });
    }

    private void writeNewMenu(String url, String menuname, String menucost, String menukcal) {
        System.out.println(url+menuname+menucost+menukcal);
        Drink infor = new Drink();
        infor.ImageUrl =url;
        infor.name = menuname;
        infor.cost = menucost;
        infor.kcal = menukcal;
        mDatabase.child("Menu").push().setValue(infor);
        dismiss();
    }

    private void motify(){
        Drink infor = new Drink();
        infor.ImageUrl =pathdata;
        infor.name = name.getText().toString();
        infor.cost = cost.getText().toString();
        infor.kcal = kcal.getText().toString();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Menu/" + key, infor.toMap());
        mDatabase.updateChildren(childUpdates);
    }

}

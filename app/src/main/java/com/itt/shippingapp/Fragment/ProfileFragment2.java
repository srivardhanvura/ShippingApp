package com.itt.shippingapp.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itt.shippingapp.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static androidx.core.content.ContextCompat.getSystemService;

public class ProfileFragment2 extends Fragment {

    String id, name, email, phone, url;

    ProfileFragment2(String a, String b, String c, String d, String e) {
        id = a;
        name = b;
        email = c;
        phone = d;
        url = e;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile2, container, false);
    }


    String[] perms = {"android.permission.READ_EXTERNAL_STORAGE"};
    int permsRequestCode = 200;
    CircleImageView circle;
    Uri uri;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText nameTxt = view.findViewById(R.id.pro_name2);
        final EditText emailTxt = view.findViewById(R.id.pro_email2);
        final EditText phoneTxt = view.findViewById(R.id.pro_num2);
        circle = view.findViewById(R.id.pic2);
        final Button submit = view.findViewById(R.id.subPro);
        final Button edit = view.findViewById(R.id.editBtn);
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        nameTxt.setText(name);
        nameTxt.setFocusable(false);
        emailTxt.setText(email);
        emailTxt.setFocusable(false);
        phoneTxt.setText(phone);
        if (!url.isEmpty())
            Picasso.get().load(url).fit().centerCrop()
                    .error(R.drawable.dp)
                    .placeholder(R.drawable.dp)
                    .into(circle);

        edit.setOnClickListener(v -> {

            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle("Choose");
            dialog.setMessage("Select image from gallery or capture an image");
            dialog.setPositiveButton("Open Gallery", (dialog12, which) -> {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 123);
            });
            dialog.setNeutralButton("Open Camera", (dialog1, which) -> CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(getContext(), ProfileFragment2.this));
            dialog.create();
            dialog.show();
        });

        submit.setOnClickListener(v -> {

            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle("Submit?");
            dialog.setMessage("Are you sure you want to save changes?");
            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!phoneTxt.getText().toString().trim().equals(phone)) {
                        db.child("Users").child(id).child("Phone").setValue(phoneTxt.getText().toString());
                    }
                    if (uri != null) {
                        final StorageReference storage = FirebaseStorage.getInstance().getReference().child("Profile Pictures").child(id + "." + getExtn());
                        storage.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        db.child("Users").child(id).child("Photo").setValue(uri.toString());
                                    }
                                });
                            }
                        });
                    }
                    getFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment()).commit();
                }
            });
            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment()).commit();
                }
            });
            dialog.create();
            dialog.show();
        });
    }

    private String getExtn() {
        ContentResolver content = getActivity().getContentResolver();
        MimeTypeMap map = MimeTypeMap.getSingleton();
        return map.getExtensionFromMimeType(content.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123 && resultCode == RESULT_OK) {
            uri = data.getData();
            CropImage.activity(uri)
                    .start(getContext(), ProfileFragment2.this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uri = result.getUri();
                circle.setImageURI(uri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200 && grantResults.length > 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else
                Toast.makeText(getActivity(), "Permission rejected", Toast.LENGTH_SHORT).show();
        }
    }
}
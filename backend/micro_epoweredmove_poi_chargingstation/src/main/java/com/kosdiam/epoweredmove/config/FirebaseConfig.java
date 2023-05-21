package com.kosdiam.epoweredmove.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import jakarta.annotation.PostConstruct;

@Component
public class FirebaseConfig {
    @Bean
    public DatabaseReference firebaseDatabase(){
        return FirebaseDatabase.getInstance().getReference();
    }
    @Bean
    public FirebaseAuth firebaseAuth(){
        return FirebaseAuth.getInstance();
    }
    @Bean
    public Storage firebaseStorage(){ return storageOptions.getService(); }

    private StorageOptions storageOptions;
    @Value("${com.kosdiam.firebase.database.url}")
    private String databaseUrl;
    @Value("${com.kosdiam.firebase.config.path}")
    private String configPath;
    @Value("${com.kosdiam.firebase.project.id}")
    private String projectId;

    @PostConstruct
    public void init(){
        try{
            initFirebaseApp();
            initFirebaseStorage();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initFirebaseApp() throws IOException {
        var serviceAccount = FirebaseConfig.class.getClassLoader().getResourceAsStream(configPath);
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl(databaseUrl)
                .build();
        FirebaseApp.initializeApp(options);
    }
    private void initFirebaseStorage() throws IOException {
        var serviceAccount = FirebaseConfig.class.getClassLoader().getResourceAsStream(configPath);
        storageOptions = StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setProjectId(projectId)
                .build();
    }
}

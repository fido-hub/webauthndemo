package com.google.webauthn.gaedemo.storage;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.gson.*;
import com.google.webauthn.gaedemo.exceptions.ResponseException;
import com.google.webauthn.gaedemo.objects.PublicKeyCredential;
import com.googlecode.objectify.*;
import com.googlecode.objectify.annotation.*;
import java.util.List;

@Entity
public class Credential {
  @Parent
  Key<User> user;
  @Id
  public Long id;

  private int signCount;
  private PublicKeyCredential credential;

  public Credential() {
    signCount = 0;
  }

  public Credential(String json) {
    signCount = 0;
    Gson gson = new Gson();
    credential = gson.fromJson(json, PublicKeyCredential.class);
  }

  public Credential(PublicKeyCredential credential) {
    signCount = 0;
    this.credential = credential;
  }

  public void validate() throws ResponseException {
    if (credential == null) {
      throw new ResponseException("Credentials invalid");
    }
  }

  public String toJson() {
    Gson gson = new Gson();
    return gson.toJson(credential);
  }

  public void save(String currentUser) {
    Key<User> user = Key.create(User.class, currentUser);
    this.user = user;
    ofy().save().entity(this).now();
  }

  public static List<Credential> load(String currentUser) {
    Key<User> user = Key.create(User.class, currentUser);
    List<Credential> credentials = ofy().load().type(Credential.class).ancestor(user).list();
    return credentials;
  }

  public static void remove(String currentUser, String id) {
    Key<User> user = Key.create(User.class, currentUser);
    ofy().delete().type(Credential.class).parent(user).id(Long.valueOf(id)).now();
  }

  /**
   * @return the credential
   */
  public PublicKeyCredential getCredential() {
    return credential;
  }

  /**
   * @return the signCount
   */
  public int getSignCount() {
    return signCount;
  }

  /**
   * @param signCount
   */
  public void updateSignCount(int signCount) {
    this.signCount = signCount;
    ofy().save().entity(this).now();
  }

}

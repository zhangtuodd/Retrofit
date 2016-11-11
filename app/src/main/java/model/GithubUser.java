package model;
import com.google.gson.annotations.SerializedName;

public class GithubUser {
    @SerializedName("id")
    public String mId;
    @SerializedName("login")
    public String mLogin;
    @SerializedName("avatar_url")
    public String mAvatarUrl;

    @Override
    public String toString() {
        return "GithubUser{" +
                "mId='" + mId + '\'' +
                ", mLogin='" + mLogin + '\'' +
                ", mAvatarUrl='" + mAvatarUrl + '\'' +
                '}';
    }
}

package event;



import java.util.List;

import model.GithubUserDetail;



public class UserDetailsLoadedCompleteEvent {
    private List<GithubUserDetail> mGithubUserDetails;

    public UserDetailsLoadedCompleteEvent(List<GithubUserDetail> githubUserDetails) {
        mGithubUserDetails = githubUserDetails;
    }

    public List<GithubUserDetail> getGithubUserDetails() {
        return mGithubUserDetails;
    }
}

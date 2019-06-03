import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import feign.Feign;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;
import feign.gson.GsonDecoder;

/**
 * @author Meifans
 */
public class FeignApplication {

    public static void main(String[] args) {
        GitHub gitHub = Feign.builder()
                             .decoder(new GsonDecoder())
                             .target(GitHub.class, "https://api.github.com");

        contributors(gitHub, "OpenFeign", "feign");

        repos(gitHub, "meifans", "owner");
    }

    private static void repos(GitHub gitHub, String user, String type) {
        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.put("type", type);
        List<Repo> repos = gitHub.repos(user, queryMap);
        for (Repo repo : repos) {
            System.out.println(repo.htmlUrl);
        }
    }

    private static void contributors(GitHub gitHub, String owner, String repo) {
        // 相当于 curl https://api.github.com/repos/OpenFeign/feign/contributors
        List<Contributor> contributors = gitHub.contributors(owner, repo);
        String first = contributors.stream().findFirst().map(c -> c.login + "(" + c.contributions + ")").orElse("");
        System.out.println(first);
    }

    interface GitHub {
        @RequestLine("GET /repos/{owner}/{repo}/contributors")
        List<Contributor> contributors(@Param("owner") String owner, @Param("repo") String repo);

        @RequestLine("GET /users/{user}/repos")
        List<Repo> repos(@Param("user") String owner, @QueryMap Map<String, String> queryMap);
    }

    static class Contributor {
        String login;
        int contributions;
    }

    static class Repo {
        @SerializedName("html_url")
        String htmlUrl;
    }
}

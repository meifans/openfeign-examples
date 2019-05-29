import java.util.List;

import feign.Feign;
import feign.Param;
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

        // 相当于 curl https://api.github.com/repos/OpenFeign/feign/contributors
        List<Contributor> contributors = gitHub.contributors("OpenFeign", "feign");

        for (Contributor c : contributors) {
            System.out.println(c.login + "(" + c.contributions + ")");
        }
    }

    interface GitHub {
        @RequestLine("GET /repos/{owner}/{repo}/contributors")
        List<Contributor> contributors(@Param("owner") String owner, @Param("repo") String repo);
    }

    static class Contributor {
        String login;
        int contributions;
    }
}

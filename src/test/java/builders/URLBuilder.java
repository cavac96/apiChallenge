package builders;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class URLBuilder {
    private String trelloDomain;
    private List<String> path;

    public URLBuilder(){
        path = new ArrayList<>();
    }

    public URLBuilder addDomain(String domain){
        this.trelloDomain = domain;
        return this;
    }

    public URLBuilder addPathStep(String step){
        path.add(step);
        return this;
    }

    public URL build(){
        try {
            return new URL(trelloDomain+"/"+String.join("/", path));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

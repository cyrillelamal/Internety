import com.cyrillelamal.internety.Fillers.AsynchronousFillerInterface;
import com.cyrillelamal.internety.Fillers.FutureFiller;
import com.cyrillelamal.internety.Serializers.SerializerInterface;
import com.cyrillelamal.internety.Serializers.TextSerializer;
import com.cyrillelamal.internety.SiteMap;

import java.net.URI;

public class Main {
    final static String HREF = "https://cheatsheetseries.owasp.org/cheatsheets/";

    public static void main(String[] args) throws Exception {
        URI start = new URI(HREF);

        SiteMap map = new SiteMap(start);
        AsynchronousFillerInterface filler = new FutureFiller(map);

        filler.fill().synchronize();

        SerializerInterface serializer = new TextSerializer();

        String result = map.serialize(serializer);
        // The strategy pattern.
        // This is equals to
        // serializer.serialize(map)

        System.out.println(result);
    }
}

import com.cyrillelamal.internety.Fillers.AsynchronousFillerInterface;
import com.cyrillelamal.internety.Fillers.ThreadPoolFiller;
import com.cyrillelamal.internety.Serializers.SerializerInterface;
import com.cyrillelamal.internety.Serializers.TxtSerializer;
import com.cyrillelamal.internety.Serializers.XMLSerializer;
import com.cyrillelamal.internety.SiteMap;

import java.net.URI;

public class Main {
    final static String HREF = "https://cheatsheetseries.owasp.org/";
    final static int N_THREADS = 16;

    public static void main(String[] args) throws Exception {
        var start = new URI(HREF);

        var siteMap = new SiteMap(start);
        AsynchronousFillerInterface filler = new ThreadPoolFiller(siteMap, N_THREADS);

        filler.fill().await();

        // Just for demonstration purposes
        SerializerInterface serializer = Math.random() > 0.5
                ? new TxtSerializer()
                : new XMLSerializer();

        String result = siteMap.serialize(serializer);
        // The strategy pattern
        // This is equals to
        // serializer.serialize(map)

        System.out.println(result);
    }
}

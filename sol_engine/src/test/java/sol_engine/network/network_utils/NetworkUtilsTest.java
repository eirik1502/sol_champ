package sol_engine.network.network_utils;


import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;


public class NetworkUtilsTest {

    @Test
    public void testParseQueryParams() {
        String url1 = "www.kaka.com/hei/nei?kaka=10&prra=";
        String url2 = "/?kaka=10&prra=";

        Map<String, String> query1 = NetworkUtils.parseQueryParams(url1);
        assertThat(query1.containsKey("kaka"), is(true));
        assertThat(query1.containsKey("prra"), is(true));
        assertThat(query1.get("kaka"), is("10"));
        assertThat(query1.get("prra"), is(""));

        Map<String, String> query2 = NetworkUtils.parseQueryParams(url2);
        assertThat(query2.containsKey("kaka"), is(true));
        assertThat(query2.containsKey("prra"), is(true));
        assertThat(query2.get("kaka"), is("10"));
        assertThat(query2.get("prra"), is(""));
    }
}

package io.caspercommunity;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class TestProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {

	Map<String,Object> map = 	exchange.getMessage().getHeaders();
	

	for (String key : map.keySet()) {
        System.out.println(key + ":" + map.get(key));
    }
	
	}

}

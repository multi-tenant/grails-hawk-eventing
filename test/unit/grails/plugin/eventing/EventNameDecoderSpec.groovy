package grails.plugin.eventing

import spock.lang.Unroll;
import grails.plugin.spock.UnitSpec

/**
 * 
 * @author Kim A. Betti <kim.betti@gmail.com>
 */
class EventNameDecoderSpec extends UnitSpec {


	@Unroll("Decoding event name #fullEventName into #expectedEvents")
	def "decoding"() {
		given:
			def decoder = new EventNameDecoder(fullEventName)
			def generatedEventNames = [ ]
			while (decoder.hasNext())
				generatedEventNames << decoder.next()
		
			println "Generated: " + generatedEventNames
				
		expect:
			generatedEventNames.size() == expectedEvents.size()
			expectedEvents.each { expectedEventName ->
				assert generatedEventNames.contains(expectedEventName), "expected to find " + expectedEventName
			}
		
		where:
			fullEventName			| expectedEvents
			'a'						| [ 'a' ]
			'a.b.c'					| [ 'a', 'a.b', 'a.b.c' ]
	}
	
	
}

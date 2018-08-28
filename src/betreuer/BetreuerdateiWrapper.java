
package betreuer;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import betreuer.Betreuer;

/**
 * Mit dieser Klasse bereiten wir xml-Dateien vor
  */
@XmlRootElement(name = "betreuerdaten")
public class BetreuerdateiWrapper {

	    private List<Betreuer> betreuerdaten;

	    @XmlElement(name = "betreuersdaten")
	    public List<Betreuer> getBetreuersdaten() {
	        return betreuerdaten;
	    }

	    public void setBetreuerdaten(List<Betreuer> betreuersdaten) {
	        this.betreuerdaten = betreuersdaten;
	    }
	}





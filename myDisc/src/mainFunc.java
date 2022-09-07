
import java.util.Scanner;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

/**
 * Discord eigenes Programmierprojekt...Verwendete API JDA -> Java Discord API
 * 
 * @author Fabian Schröer
 * @version final Version
 */

public class mainFunc {

	// konstantes Datenobjekt jda, wird in mainFunc initialisiert
	
	private JDA jda;
	

	/**
	 * main so wie man sie kennt welche instanz von mainFunc erstellt und mainfunc
	 * beinhaltet jda und herunterfahren
	 * 
	 * @exception siehe Funktion mainFunc()
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			// instanz von mainFunc mit jda objekt und herunterfahren
			new mainFunc();

		} catch (LoginException e) {

			e.printStackTrace();
		} catch (IllegalArgumentException e) {

			e.printStackTrace();
		}

	}

	/**
	 * API und Bot... hier wird unser Bot geschaffen und Zugang zur API
	 * gewährleistet Der bot benötigt einen Token welcher eindeutig ist und welchen
	 * man nicht weitergeben sollte, da man auf der discord seite mit dem token die
	 * rechte des Bots ändern kann
	 * 
	 * Der Token kommt vom Discord Developer Portal, dient dazu das der Bot als Bot
	 * erkannt wird
	 * 
	 * Datentyp JDA gewährleistet zugriff zur API und allen Funktion von Java
	 * Discord API (JDA)
	 * 
	 * 
	 * @throws LoginException           falls der Token falsch ist
	 * @throws IllegalArgumentException falls kein Token vorhanden ist
	 */
	public mainFunc() throws LoginException, IllegalArgumentException {

		this.jda = JDABuilder.createDefault("OTgwMDg1ODcxMjYyOTY5ODU2.GgaE-p.FlCaDGWQJHsOEgUjbd_dmvOp1H6PBURAXP3BUA")
				.setActivity(Activity.listening("Fabi is coding me")).addEventListeners(new BotListener())

				.build();

		System.out.println("Bot ist Online!");
		System.out.println("Gib exit ein, um den Bot herunterzufahren");

		herunterfahren();

	}

	/**
	 * Problem: falls der bot gestartet wird, läuft er weiter auch wenn Eclipse
	 * geschlossen wird... er läuft dann noch 3-5min weiter... öffnet man eclipse
	 * erneut und startet den bot so sind 2 Bots online und der Bot druckt alles 2x
	 * aus ebenso wenn man einfach den bot mehermals startet in dem man in Eclipse
	 * auf run klickt
	 * 
	 * 
	 * Lösung hierfür ist diese Funktion. Sie ist vorallem sinnvoll für
	 * Programmierer damit der Bot nicht noch 10 min weiter l�uft im Hintergrund PC
	 * wurde ohne die Funktion mehrmals sehr laut weil ich den bot 20x im
	 * Hintergrund offen hatte, er terminiert nicht von selbst
	 * 
	 * Funktion: sobald man den Bot beenden möchte gibt man hier in die konsole exit
	 * oder clear ein und darf danach eclipse schließen oder den Bot neustarten
	 * 
	 * sollte der Bot zwei mal laufen dann einfach Eclipse schließen und 3-5min
	 * warten
	 */
	public void herunterfahren() {
		// Scanner für Konsolen eingabe
		Scanner sc = new Scanner(System.in);
		String eingabe = sc.next();
		String line = "";
		// solange eingabe nicht mit line übereinstimmt lasse diese Funktion laufen
		// falls exit und clear irgendwann kommt -> bot abschalten und und aus der
		// Schleife ausbrechen
		while ((line = eingabe) != null) {
			if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("clear")) {
				if (jda != null) {

					// Fährt die den Bot und die Verbindung zur DiscordAPI herunter
					jda.shutdown();
					System.out.println("Bot wurde erfolgreich heruntergefahren.");
					System.out.println("Programm terminiert.");
					break;
				}

			} else {
				line = null;
				eingabe = null;
				System.out.println("Sie dürfen nur Exit oder Clear eingeben");
				// Bei falscher eingabe wird alles auf null gesetzt und weiterhin auf exit oder
				// clear gewartet
				herunterfahren();

			}

		}

	}

}

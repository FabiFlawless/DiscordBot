import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Wichtige Informationen allgemein zum Projekt. ListenerAdapter ist ein
 * Interface welches benötigt wird um Nachrichten aus dem Discord (Events)
 * auszulesen. queue() steht am Ende einer jeden Ausgabe (fast), diese Funktion
 * führt die Nachricht aus und checkt ob derzeit noch andere Sachen laufen
 * falls ja dann soll der Bot die Anweisung einfach hinten ran in der Queue
 * stellen und ausführen wenn vorhergehende auch fertig sind.
 * 
 * @author Fabian Schröer
 *
 */

public class BotListener extends ListenerAdapter {

	// Prefix für alle Kommandos welche in BotListener sind
	// Kommandos sollen alle mit <!> anfangen
	private static String prefix = "!";

	/**
	 * Begruessungsfunktion Programm liest Wörter aus dem filePath (einer Liste von
	 * Begrüßungen), und vergleicht sie mit der Nachricht des Nutzers.Wenn eine
	 * Begrüßung in der Nachricht ist, so antwortet der Bot dem Nutzer mit einer
	 * zufälligen Begrüßung.
	 * 
	 * @author Fabian Schröer
	 * @param event ist ein Datenobjekt welches in einem Textchannel des Discords
	 *              mitgeschnitten wurde
	 * @throws IOException wird geworfen wenn etwas mit der Datei Salutations.txt
	 *                     nicht stimmt oder sie gar nicht vorhanden ist
	 * 
	 */
	public void salutations(MessageReceivedEvent event) {

		// auslesen von salutations.txt und speichern in einem String
		String content = "hallo Hallo moin Moin hi Hi HI hey servus hey";
		// String in eine ArrayList umwandeln
		List<String> myList = new ArrayList<String>(Arrays.asList(content.split(" ")));
		// Nutzereingabe im Discord
		String message = event.getMessage().getContentStripped();

		// iteriere durch die Liste und vergleiche ob eines der Wörter aus der Liste
		// enthalten ist in der User Nachricht
		for (int i = 0; i < myList.size(); i++) {
			String suche = myList.get(i);

			if (message.contains(suche)) {
				// zufallszahl um eine gewisse vielfältigkeit zu simulieren
				Random rnd = new Random();
				int answer = rnd.nextInt(3) + 1;

				if (answer == 3) {
					// sendtyping an sich sinnlos aber lustig, im Discord steht im Chat, dass der
					// Bot gerade schreibt
					event.getChannel().sendTyping().queue();
					event.getChannel().sendMessage("Guten Tag " + event.getMember().getAsMention() + "!").queue();
					break;
				}

				else if (answer == 2) {
					event.getChannel().sendTyping().queue();
					event.getChannel().sendMessage("Willkommen " + event.getMember().getAsMention() + "!").queue();
					break;
				}

				else if (answer == 1) {
					event.getChannel().sendTyping().queue();
					event.getChannel().sendMessage("Servus " + event.getMember().getAsMention() + "!").queue();
					break;

				}

			}

		}

	}

	/**
	 * Diese Funktion zeigt den Mensa Plan der HSMW mit einer vom Bot getätigten
	 * Ausgabe bei eingabe von !essen
	 * 
	 * @param event ist ein Datenobjekt welches in einem Textchannel des Discords
	 *              mitgeschnitten wurde
	 */

	public void essen(MessageReceivedEvent event) {
		// check ob eingabe des nutzers mit <!> startet akzepiert wird auch !eat..
		if (event.getMessage().getContentStripped().startsWith(prefix + "eat")
				|| (event.getMessage().getContentStripped().startsWith(prefix + "essen"))) {

			event.getChannel().sendMessage("https://www.mensaplan.de/mittweida/mensa-mittweida/index.html").queue();

		}
	}

	/**
	 * Funktion zum Auslesen der korrekten Zeit mit dem Befehl !time
	 * 
	 * @param event ist ein Datenobjekt welches in einem Textchannel des Discords
	 *              mitgeschnitten wurde
	 */
	public void time(MessageReceivedEvent event) {
		if (event.getMessage().getContentStripped().startsWith(prefix + "time")) {
			GregorianCalendar now = new GregorianCalendar();

			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
			event.getChannel().sendMessage(" " + df.format(now.getTime())).queue();

		}
	}

	/**
	 * Funktion für einen simplen Taschenrechner, er kann addieren und subtrahieren
	 * und schmeißt Error Nachrichten in Form einer Embeded Nachricht
	 * 
	 * @param event ist ein Datenobjekt welches in einem Textchannel des Discords
	 *              mitgeschnitten wurde
	 * @exception wird geschmissen, wenn nutzer falsche eingabe syntax verwendet
	 */
	public void calculator(MessageReceivedEvent event) {
		// Hier wird die Eingabe des Nutzers komplett zerstückelt und in ein String
		// array gesteckt
		String[] message = event.getMessage().getContentRaw().split(" ");
		// im ersten If Case werden alle Fehler gefiltert, falls irgendwas mit der
		// Syntax nicht stimmt produziert die Funktion hier einen Embeded Error
		// Nachricht
		try {
			if (message[0].equalsIgnoreCase(prefix + "calc")
					&& (message.length == 1 || message.length == 3 || message.length > 4 || message.length == 2)) {
				EmbedBuilder error1 = new EmbedBuilder();
				error1.setColor(Color.RED);
				error1.setTitle("Usage: " + prefix + "calc " + "add/sub [NUMBER1] [NUMBER2]");
				event.getChannel().sendTyping().queue();
				event.getChannel().sendMessageEmbeds(error1.build()).queue();
			}
			// zweiter If case ist für Addition zuständig
			else if (message[0].equalsIgnoreCase(prefix + "calc") && (message[1].equalsIgnoreCase("add"))) {
				// num1 und num2 werden aus der Nachricht des Users ausgelesen, in ints
				// umgewandelt und addiert
				int num1 = Integer.parseInt(message[2]);
				int num2 = Integer.parseInt(message[3]);
				event.getChannel().sendMessage("Addition").queue();
				event.getChannel().sendMessage("The result is " + (num1 + num2)).queue();
			}
			// dritter if case ist für subtraktion zuständig
			else if (message[0].equalsIgnoreCase(prefix + "calc") && (message[1].equalsIgnoreCase("sub"))) {
				int num1 = Integer.parseInt(message[2]);
				int num2 = Integer.parseInt(message[3]);

				event.getChannel().sendMessage("Subtraction").queue();
				event.getChannel().sendMessage("The result is " + (num1 - num2)).queue();
			}

		} catch (IndexOutOfBoundsException ex) {
			event.getChannel().sendMessage("Wrong Syntax!").queue();
		}
	}

	/**
	 * Diese Funktion ist dafür zuständig, dass falls der Bot auf einem richtigen
	 * Server laufen sollte, die Benutzer keine Beleidigungen gegenüber anderen
	 * Usern äußern können. Beleidgungen werden gelöscht und die Person wird
	 * ermahnt.
	 * 
	 * Generell funktioniert es so, dass aus einer Textdatei mit 1000+
	 * Schimpfwörtern jedes Wort untersucht wird ob es in der User Nachricht
	 * enthalten ist falls ja dann ist eine Beleidung in der Nachricht und die
	 * Nachricht wird gelöscht und der Author der Nachricht wird ermahnt und
	 * markiert im Discord
	 * 
	 * @param event ist ein Datenobjekt welches in einem Textchannel des Discords
	 *              mitgeschnitten wurde
	 * @exception Schmeißt Exception wenn die Textdatei mit den Schimpfwörtern
	 *                      nicht vorhanden ist
	 */
	public void schimpfwortfilter(MessageReceivedEvent event) {

		try {
			// Datei mit Schimpfwörtern
			Path filePath = Path.of("C:/Java/2_Semester/myDisc/src/swearword.txt/");
			// Speichern der Wörter in einem String
			String content = Files.readString(filePath);
			// Splitten der Wörter und in einer Liste speichern
			List<String> myList = new ArrayList<String>(Arrays.asList(content.split("\\r\\n")));
			// User Eingabe
			String message = event.getMessage().getContentStripped();

			for (int i = 0; i < myList.size(); i++) {
				String suche = myList.get(i);
				// Suche nach Beleidgung aus der Liste + ignorieren von groß und
				// kleinschreibung
				if (message.contains(suche)) {
					// Nachricht löschen
					event.getChannel().deleteMessageById(event.getMessageId()).queue();
					event.getChannel().sendMessage("Do not swear!" + event.getMember().getAsMention() + "!").queue();
					break;

				}

			}

		} catch (IOException e) {
			if (!event.getMessage().getAuthor().isBot()) {
				event.getChannel().sendMessage("Die Datei swearword.txt ist nicht vorhanden!").queue();
			}

		}
		
		catch (ErrorResponseException ex) {
			event.getChannel().sendMessage("Wait a bit, I am still working on your previous task!").queue();
		}
	}

	/**
	 * Funktionsweise: !clear ANZAHL löscht eine/mehrere Nachrichten aus dem Chat
	 * verlauf !clear ohne anzahl gibt eine embedded Nachricht aus, welche zeigt wie
	 * der befehl genau funktioniert falls anzahl der zu löschenden nachrichten
	 * >100 dann abbruch. Die Nachricht des Bots erfolgt nachdem die Nachrichten
	 * gelöscht wurden in Form einer Nachricht mit Zeitcounter. Dies ist zu
	 * vergleichen wie das auf popen einer Nachricht am Smartphone welche danach
	 * wieder verschwindet.
	 * 
	 * @exception Wird geschmissen falls der Chat verlauf im Discord leer ist, aber
	 *                 dennoch versucht wird Nachrichten zu löschen oder wenn der
	 *                 BOt derzeit noch arbeitet, er meldet sich wenn er gerade noch
	 *                 dabei ist die eine Nachricht anzuzeigen, dann aber zu
	 *                 loeschen. Kommt in dieser Zeit ein zweiter Befehl wird die
	 *                 RestAction Exception geschmissen
	 *
	 * @param event ist ein Datenobjekt welches in einem Textchannel des Discords
	 *              mitgeschnitten wurde
	 */
	public void clear(MessageReceivedEvent event) {
		// checken ob nachricht mit dem Prefix <!> beginnt
		if (event.getMessage().getContentStripped().startsWith(prefix)) {
			String[] args = event.getMessage().getContentRaw().split("\\s+");

			try {
				// Benutzeranleitung falls nur !clear ohne Anzahl eingegeben wird oder mit zu
				// vielen Eingabe Parametern
				if (args[0].equalsIgnoreCase(prefix + "clear")) {
					if (args.length < 2 || args.length > 2) {
						// EmbedBuilder objekt erstellen und Farben und Inhalt einstellen
						EmbedBuilder error1 = new EmbedBuilder();
						error1.setColor(Color.RED);
						error1.setTitle("Specify # of messages to delete!");
						error1.setDescription("Usage: " + prefix + "clear [# of messages]");
						// embed nachricht abschicken
						event.getChannel().sendMessageEmbeds(error1.build()).queue();
					}
					// bei zu hoher eingabe wird der Nutzer darauf hingewiesen weniger Nachrichten
					// zu löschen, da bei <100 der bot oder discord abstürzen kann
					// eingabe von 99 dauert schon mehrere Sekunden
					// nachricht auch hier als embded Error
					else if (Integer.parseInt(args[1]) >= 100 || Integer.parseInt(args[1]) < 1) {
						EmbedBuilder error2 = new EmbedBuilder();
						error2.setColor(Color.RED);
						error2.setTitle("Only 1-100 messages can be deleted!");
						error2.setDescription("Usage: " + prefix + "clear [1-100]");
						event.getChannel().sendMessageEmbeds(error2.build()).queue();
					}
					// Eigenlichhes Löschen der nachrichten aus dem Discord verlauf (Nachrichten
					// dürfen nicht älter als 2 Wochen im discord chat sein)

					// anzah auslesen aus usereingabe
					else if (Integer.parseInt(args[1]) < 100 && Integer.parseInt(args[1]) >= 1) {
						int values = Integer.parseInt(args[1]);
						values = values + 1;
						// !clear ist auch eine Nachricht welche auch verschwinden soll
						event.getMessage().delete();
						// Nachrichten history des text channels auslesen bis zur anzahl der
						// übergebenen
						// Zahl des Users hier .complete nicht .queue weil die clear funktion vorang hat
						// und direkt ausgeführt werden soll und nicht erst in eine queue geraten soll
						// alle Nachrichten in einer liste speichern...
						List<Message> messages = event.getChannel().getHistory().retrievePast(values).complete();
						// alle ausgelesenen nachrichten der vergangenheit löschen
						event.getTextChannel().deleteMessages(messages).queue();
						// info ausgabe tätigen, info ausgabe verschwindet nach 5 sekunden
						event.getChannel().sendMessage(" " + args[1].toString() + " messages deleted!")
								.queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
					}
				}
			} catch (IllegalArgumentException ex) {

				event.getChannel().sendMessage("There are no messages to delete!")
						.queue(m -> m.delete().queueAfter(3, TimeUnit.SECONDS));
			}

			catch (ErrorResponseException ex) {
				event.getChannel().sendMessage("Wait a bit, I am still working on your previous task!").queue();
			}

		}

	}

	/**
	 * Hilfsfunktion für neue User im Discord um sich einen Überblick zu
	 * verschaffen Listet alle Funktionen auf die ein User tätigen kann mit
	 * erklärung in form von embed nachrichten. Funktioniert mit !help oder
	 * !commands
	 * 
	 * @param e ist ein Datenobjekt welches in einem Textchannel des Discords
	 *          mitgeschnitten wurde
	 */
	public void listCommands(MessageReceivedEvent e) {
		// check ob Nachricht mit prefix <!> beginnt
		if (e.getMessage().getContentStripped().startsWith(prefix)) {
			// nachricht in array speichern
			String[] args = e.getMessage().getContentRaw().split("\\s+");
			// Syntax check für help / commands befehl
			if (args[0].equalsIgnoreCase(prefix + "commands") || (args[0].equalsIgnoreCase(prefix + "help"))) {
				// embed 1 erstellen
				EmbedBuilder command1 = new EmbedBuilder();
				command1.setColor(Color.GREEN);
				command1.setTitle("Clear Command");
				command1.setDescription("Usage: " + prefix
						+ "clear [1-100] \n The clear command can delete a number of messages in the chat");

				e.getChannel().sendMessageEmbeds(command1.build()).queue();
				// embed 2 erstllen
				EmbedBuilder command2 = new EmbedBuilder();
				command2.setColor(Color.GREEN);
				command2.setTitle("Food Mensa HSMW");
				command2.setDescription("Usage: " + prefix + "eat / essen \n Shows the mensa plan for HSMW");
				e.getChannel().sendMessageEmbeds(command2.build()).queue();
				// embed 3 erstellen
				EmbedBuilder command3 = new EmbedBuilder();
				command3.setColor(Color.GREEN);
				command3.setTitle("Time MESZ");
				command3.setDescription("Usage: " + prefix + "time \n Shows the time for MESZ");
				e.getChannel().sendMessageEmbeds(command3.build()).queue();
				// embed 4 erstllen
				EmbedBuilder command4 = new EmbedBuilder();
				command4.setColor(Color.GREEN);
				command4.setTitle("Simple Calculator");
				command4.setDescription("Usage: " + prefix
						+ "calc add/sub [int 1] [int 2] \n provides the add and sub function of a calculator");
				e.getChannel().sendMessageEmbeds(command4.build()).queue();
				// embed 5 erstellen
				EmbedBuilder command5 = new EmbedBuilder();
				command5.setColor(Color.GREEN);
				command5.setTitle("User Info");
				command5.setDescription("Usage: " + prefix + "userinfo @Name_Of_User");
				e.getChannel().sendMessageEmbeds(command5.build()).queue();
			}
		}

	}

	/**
	 * Userinfo funktion gibt Auskunft über Member des Discords zb wann der nutzer
	 * dem discord beigetreten ist oder welchen avatar er benutzt...
	 * !userinfo @Discord_Username benötigt wird hier eine eindeutige User ID
	 * welche von Discord vergeben wird
	 * 
	 * @param e ist ein Datenobjekt welches in einem Textchannel des Discords
	 *          mitgeschnitten wurde
	 * @exception IndexOutOfBoundsException Fehler bei der Eingabe werden hier
	 *                                      gecatcht und es wird auf die richtige
	 *                                      Syntax verwiesen, falls kein nutzer
	 *                                      markiert wird für den man Info möchte
	 * 
	 * @exception NumberFormatException     wird geschmissen wenn nach !userinfo
	 *                                      garkeine richtige ID eingegeben wird
	 *                                      oder die ID garkein Member vom Discord
	 *                                      ist
	 */
	public void userinfo(MessageReceivedEvent e) {
		String id = "";
		// check ob syntax des Users stimmt id ist hier einfach ein leerer String zu
		// beginn und wird immer akzeptiert
		try {
			if (e.getMessage().getContentStripped().startsWith(prefix + "userinfo" + id)) {

				// markierten Benutzer in eine User Datei speichern in dem man die funktion
				// getMentionMembers an Stelle 0 nimmt
				// getMentionMembers gibt eine MemberListe zurück hier greift man einfach auf
				// den 0. Eintrag welcher ja der markierte Benutzer ist
				Member user = e.getMessage().getMentionedMembers().get(0);
				// Embed erstellen für strukturierte Ausgabe
				EmbedBuilder showInfo = new EmbedBuilder();
				// author in diesem der Member von dem Info verlangt wird
				// setAuthor ist in embeds eine Art Überschrift welche hier ausgenutzt wird zu
				// Übersichtlichkeit
				showInfo.setAuthor(user.getUser().getName(), user.getUser().getAvatarUrl(),
						user.getUser().getEffectiveAvatarUrl());
				// setDescription ist der Hauptteil einer Embed Nachricht <**> lässt buchstaben
				// fett ausdrucken \n normal als new line
				showInfo.setDescription("**Mention**: " + user.getAsMention() + "\n**DiscordTag**: "
				// getAsTag ist der markierte @Username#Zahlencode welcher eindeutig auf einen
				// Member verweist wie die ID
				// Tag als eine Pointer für die ID
						+ user.getUser().getAsTag() + "\n**ID**: " + user.getId() + "\n**Avatar Link**: [Link]("
						+ user.getUser().getAvatarUrl() + ")");
				// anzeige des Profilbilds in Discord in größerer Form
				showInfo.setThumbnail(user.getUser().getEffectiveAvatarUrl());
				// feld für beitrittsdatum
				showInfo.addField("Joined Server: ",
						user.getTimeJoined().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), true);
				// feld für Account erstellung
				showInfo.addField("Account Created: ",
						user.getTimeCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), false);
				// embed erstellen und abschicken
				e.getChannel().sendMessageEmbeds(showInfo.build()).queue();
			}
		} catch (IndexOutOfBoundsException ex) {
			e.getChannel().sendMessage("**Please enter an Username!**").queue();
			EmbedBuilder command = new EmbedBuilder();
			command.setColor(Color.GREEN);
			command.setTitle("User Info");
			command.setDescription("Usage: " + prefix + "userinfo @Name_Of_User");
			e.getChannel().sendMessageEmbeds(command.build()).queue();
		} catch (NumberFormatException ex) {
			e.getChannel().sendMessage("**Please enter a valid User ID!**").queue();
		}

	}

	/**
	 * Wikipedia Artikel anzeigen lassen, für einen Suchstring welchen der User im
	 * Discord übergibt
	 * 
	 * @param e ist ein Datenobjekt welches in einem Textchannel des Discords
	 *          mitgeschnitten wurde
	 */
	public void wiki(MessageReceivedEvent e) {

		String searchstring = "";
		if (e.getMessage().getContentStripped().startsWith(prefix + "wiki")) {
			String[] args = e.getMessage().getContentRaw().split("\\s+");
			String[] copy_ofArgs = Arrays.copyOfRange(args, 1, args.length);
			searchstring = String.join("_", copy_ofArgs);
			e.getChannel().sendMessage("https://de.wikipedia.org/wiki/" + searchstring).queue();

		}
	}

	/**
	 * "Die Main methode" des EventListensers, hier werden alle Funktionen die den
	 * Textchannel abhören abgerufen und für laufbereit erklärt
	 */
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		userinfo(event);
		salutations(event);
		essen(event);
		schimpfwortfilter(event);
		clear(event);
		time(event);
		calculator(event);
		listCommands(event);
		wiki(event);
	}

}

package controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import actors.Peer;
import actors.Peer.Acquaintance;
import actors.Supervisor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.model.MediaTypes;
import akka.pattern.Patterns;
import akka.util.Timeout;
import drools.Exchanges;
import messages.CustomQueryMessage;
import messages.DumpStatesMessage;
import messages.SupervisorMessage;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import referral_helper.ReferralUtil;
import scala.compat.java8.FutureConverters;

/**
 * This controller contains an action to handle HTTP requests to the
 * application's home page.
 */

@Singleton
public class HomeController extends Controller {

	private ActorSystem system;

	private ActorRef supervisor;

	@Inject
	public HomeController(ActorSystem system) throws IOException {
		this.system = system;
		this.supervisor = system.actorOf(Supervisor.getProps());

		Path newFilePath = Paths.get("app/drools/exchanges.txt");

		Files.deleteIfExists(newFilePath);

		Files.createFile(newFilePath);
	}

	/**
	 * An action that renders an HTML page with a welcome message. The
	 * configuration in the <code>routes</code> file means that this method will
	 * be called when the application receives a <code>GET</code> request with a
	 * path of <code>/</code>.
	 */
	public Result index() {
		return ok("Welcome to referrals");
	}

	@BodyParser.Of(BodyParser.Json.class)
	public Result createActors() throws InterruptedException, ExecutionException {

		SortedSet<String> peers = new TreeSet<String>();
		try {
			JsonNode json = request().body().asJson();

			Iterator<JsonNode> it = json.elements();

			while (it.hasNext()) {
				JsonNode entry = it.next();

				String name = entry.get("name").asText();

				double[] expertise = new ObjectMapper().readValue(entry.get("expertise").toString(), double[].class);

				double[] needs = new ObjectMapper().readValue(entry.get("needs").toString(), double[].class);

				LinkedList<Acquaintance> neighbours = new ObjectMapper().readValue(entry.get("neighbors").toString(),
						new TypeReference<LinkedList<Acquaintance>>() {
						});

				system.actorOf(Peer.getProps(name, expertise, needs, neighbours), name);

				peers.add(name);

			}

			SupervisorMessage supMessage = new SupervisorMessage(25, peers);

			ReferralUtil.logDroolsMessage(Exchanges.HOME_CONTROLLER, Exchanges.SEND, "AutoGenerateQuery",
					Exchanges.TELL);

			supervisor.tell(supMessage, supervisor);

			ObjectNode response = Json.newObject();

			response.put("status", "success");

			return ok(response);
		} catch (Exception e) {

			ObjectNode obj = Json.newObject();
			obj.put("status", "error");
			obj.put("message", e.getMessage());

			return ok(obj);
		}

	}

	public Result queryActor(String actor, String needs) {

		try {
			System.out.println(needs);
			double[] needsArr = new ObjectMapper().readValue("[" + needs + "]", double[].class);

			ReferralUtil.logDroolsMessage(Exchanges.HOME_CONTROLLER, Exchanges.SEND,
					Exchanges.ASK_FOR_ACTOR_REF + actor,
					!Exchanges.TELL);

			ActorRef ref = FutureConverters
					.toJava(system.actorSelection("user/" + actor).resolveOne(new Timeout(1, TimeUnit.MINUTES)))
					.thenApply(result -> (ActorRef) result).toCompletableFuture().get();

			ReferralUtil.logDroolsMessage(Exchanges.HOME_CONTROLLER, !Exchanges.SEND,
					Exchanges.RECEIVED_ACTOR_REF + actor,
					!Exchanges.TELL);

			ReferralUtil.logDroolsMessage(Exchanges.HOME_CONTROLLER, Exchanges.SEND, Exchanges.QUERY + actor,
					!Exchanges.TELL);

			String response = FutureConverters
					.toJava(Patterns.ask(ref, new CustomQueryMessage(needsArr), new Timeout(100, TimeUnit.SECONDS)))
					.thenApply(result -> (String) result).toCompletableFuture().get();

			ReferralUtil.logDroolsMessage(Exchanges.HOME_CONTROLLER, !Exchanges.SEND, Exchanges.RESPONSE + actor,
					!Exchanges.TELL);

			return ok(response).as(MediaTypes.APPLICATION_JSON.toString());

		} catch (Exception e) {
			ObjectNode obj = Json.newObject();
			obj.put("status", "error");
			obj.put("message", e.getMessage());

			return ok(obj);

		}
	}

	public Result getActorState(String actor) {

		try {

			ReferralUtil.logDroolsMessage(Exchanges.HOME_CONTROLLER, Exchanges.SEND,
					Exchanges.ASK_FOR_ACTOR_REF + actor,
					!Exchanges.TELL);

			ActorRef ref = FutureConverters
					.toJava(system.actorSelection("user/" + actor).resolveOne(new Timeout(1, TimeUnit.MINUTES)))
					.thenApply(result -> (ActorRef) result).toCompletableFuture().get();

			ReferralUtil.logDroolsMessage(Exchanges.HOME_CONTROLLER, !Exchanges.SEND,
					Exchanges.RECEIVED_ACTOR_REF + actor,
					!Exchanges.TELL);

			ReferralUtil.logDroolsMessage(Exchanges.HOME_CONTROLLER, Exchanges.SEND, "GetDump" + actor,
					!Exchanges.TELL);

			String response = FutureConverters
					.toJava(Patterns.ask(ref, new DumpStatesMessage(), new Timeout(10, TimeUnit.SECONDS)))
					.thenApply(result -> (String) result).toCompletableFuture().get();

			ReferralUtil.logDroolsMessage(Exchanges.HOME_CONTROLLER, !Exchanges.SEND,
					"SendDump" + actor,
					!Exchanges.TELL);


			return ok(response).as(MediaTypes.APPLICATION_JSON.toString());

		} catch (Exception e) {
			ObjectNode obj = Json.newObject();
			obj.put("status", "error");
			obj.put("message", e.getMessage());

			return ok(obj);

		}

	}

	public Result getLog() {
		Path newFilePath = Paths.get("app/drools/exchanges.txt");

		return ok(new File(newFilePath.toString()));
	}



}

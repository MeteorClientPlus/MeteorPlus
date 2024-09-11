package nekiplay.meteorplus.features.commands;

import com.google.gson.Gson;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import nekiplay.meteorplus.features.modules.misc.ChatGPT;
import net.minecraft.command.CommandSource;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class GPTCommand extends Command {
	public GPTCommand() {
		super("gpt", "Settings for .gpt");
	}

	public class RequestData {
		public String model = "gpt-3.5-turbo";
		public Messages[] messages =  { new Messages() };
		public class Messages {
			public String role = "user";
			public String content = "Hello world";
		}
	}

	public class ResponseData {
		public String id;
		public String object;
		public int created;
		public String model;
		public ArrayList<PromptFilterResult> prompt_filter_results;
		public ArrayList<Choice> choices;
		public Usage usage;

		public class Choice{
			public int index;
			public String finish_reason;
			public Message message;
			public ContentFilterResults content_filter_results;
		}

		public class ContentFilterResults{
			public Hate hate;
			public SelfHarm self_harm;
			public Sexual sexual;
			public Violence violence;
		}

		public class Hate{
			public boolean filtered;
			public String severity;
		}

		public class Message{
			public String role;
			public String content;
		}

		public class PromptFilterResult{
			public int prompt_index;
			public ContentFilterResults content_filter_results;
		}
		public class SelfHarm{
			public boolean filtered;
			public String severity;
		}

		public class Sexual{
			public boolean filtered;
			public String severity;
		}

		public class Usage{
			public int completion_tokens;
			public int prompt_tokens;
			public int total_tokens;
		}

		public class Violence{
			public boolean filtered;
			public String severity;
		}

	}

	public class Error{
		public int code;
		public String message;
		public String tip;
		public String powered_by;
	}

	public class ErrorData {
		public Error error;
	}


	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.then(argument("prompt", StringArgumentType.string()).executes(context -> {
			MeteorExecutor.execute(() -> {
				HttpClient client = HttpClient.newHttpClient();

				String prompt = context.getArgument("prompt", String.class);
				RequestData requestData = new RequestData();
				requestData.messages[0].content = prompt;
				Gson gson = new Gson();
				ChatGPT chatGPT = Modules.get().get(ChatGPT.class);
				if (chatGPT != null) {

					String url = "";
					String token = "";
					switch (chatGPT.service.get()) {
						case NovaAI -> {
							url = "https://api.nova-oss.com/v1/chat/completions";
							token = chatGPT.token_novaai.get();
						}
						case NagaAI -> {
							url = "https://api.naga.ac/v1/chat/completions";
							token = chatGPT.token_nagaai.get();
						}
						case Custom -> {
							url =chatGPT.custom_endpoint.get();
							token = chatGPT.token_custom.get();
						}
					}


					HttpRequest request = HttpRequest.newBuilder()
						.uri(URI.create(url))
						.POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestData)))
						.setHeader("Content-Type", "application/json")
						.setHeader("Authorization", "Bearer " + token)
						.build();

					HttpResponse<String> response = null;
					try {
						response = client.send(request, HttpResponse.BodyHandlers.ofString());
					} catch (IOException e) {
						error("Request error: " + e.getMessage());
					} catch (InterruptedException e) {
						error("Request error: " + e.getMessage());
					}

					try {
						ResponseData responseData = gson.fromJson(response.body(), ResponseData.class);
						info(responseData.choices.get(0).message.content);
					}
					catch (Exception e) {
						try {
							ErrorData errorData = gson.fromJson(response.body(), ErrorData.class);
							error(errorData.error.message);
						}
						catch (Exception e2) {
							error("Error: " + e2.getMessage());
						}
					}
				}
			});

			return SINGLE_SUCCESS;
		}));
	}
}

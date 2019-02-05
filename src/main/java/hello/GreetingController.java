package hello;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	@RequestMapping("/greeting")
	@CrossOrigin(origins = "http://localhost:3000")
	public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {

		CompletableFuture<Long> thenApply = CompletableFuture.supplyAsync(this::process1)
				.thenApply(this::process2)
				.thenApply(this::process3)
				.exceptionally(ex -> {
					System.out.println("Got Some Exception " + ex.getMessage());
					System.out.println("Returning some default value");
					return new Long(0);
				});
		Long id = thenApply.join();

		return new Greeting(id, String.format(template, name));
	}

	public Long process1() {
		System.out.println(Thread.currentThread() + " process1");
		sleep(1);
		return counter.incrementAndGet();
	}

	public Long process2(Long id) {
		System.out.println(Thread.currentThread() + " process2");
		sleep(1);
		return id;
	}

	public Long process3(Long id) {
		System.out.println(Thread.currentThread() + "process3");
		sleep(1);
		return id;
	}

	private void sleep(int seconds) {
		try {
			TimeUnit.SECONDS.sleep(seconds);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

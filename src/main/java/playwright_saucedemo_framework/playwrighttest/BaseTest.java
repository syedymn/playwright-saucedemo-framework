package playwright_saucedemo_framework.playwrighttest;

	import com.microsoft.playwright.*;
	import org.testng.annotations.*;

	public class BaseTest {

	    // ThreadLocal gives each parallel thread 
	    // its own isolated instances
	    private static final ThreadLocal<Playwright> 
	        playwrightThread = new ThreadLocal<>();
	    private static final ThreadLocal<Browser> 
	        browserThread = new ThreadLocal<>();
	    private static final ThreadLocal<BrowserContext> 
	        contextThread = new ThreadLocal<>();
	    private static final ThreadLocal<Page> 
	        pageThread = new ThreadLocal<>();

	    // Protected getters — your tests use these
	    protected Page getPage() {
	        return pageThread.get();
	    }

	    @BeforeMethod
	    public void setUp() {
	        System.out.println("Setting up thread: " 
	            + Thread.currentThread().getId());

	        Playwright playwright = Playwright.create();
	        playwrightThread.set(playwright);

	        Browser browser = playwright.chromium().launch(
	            new BrowserType.LaunchOptions()
	                .setHeadless(false)
	        );
	        browserThread.set(browser);

	        BrowserContext context = browser.newContext();
	        contextThread.set(context);

	        Page page = context.newPage();
	        pageThread.set(page);

	        System.out.println("Setup complete for thread: " 
	            + Thread.currentThread().getId());
	    }

	    @AfterMethod
	    public void tearDown() {
	        System.out.println("Tearing down thread: " 
	            + Thread.currentThread().getId());
	        try {
	            if (contextThread.get() != null) {
	                contextThread.get().close();
	            }
	        } catch (Exception ignored) { }

	        try {
	            if (browserThread.get() != null) {
	                browserThread.get().close();
	            }
	        } catch (Exception ignored) { }

	        try {
	            if (playwrightThread.get() != null) {
	                playwrightThread.get().close();
	            }
	        } catch (Exception ignored) { }

	        // Clean up ThreadLocal to prevent memory leaks
	        pageThread.remove();
	        contextThread.remove();
	        browserThread.remove();
	        playwrightThread.remove();
	    }
	}
package driver;


import java.util.Map;

public class BrowserDriverRegistry {

    private final Map<String, BrowserDriverCreator> creators;

    public BrowserDriverRegistry(Map<String, BrowserDriverCreator> creators) {
        this.creators = creators;
    }

    public BrowserDriverCreator get(String browser) {
        BrowserDriverCreator creator = creators.get(browser);

        if (creator == null) {
            throw new IllegalArgumentException(
                    "Unsupported browser: " + browser
            );
        }

        return creator;
    }
}
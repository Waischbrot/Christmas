package de.rubymc.christmas.stars;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomStar {

    private static List<String> star = new ArrayList<>();

    public static String getStar() {
        return (String) star.get(ThreadLocalRandom.current().nextInt(star.size()));
    }

    static {
        star.add("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTQ2YWI3MjYxZGEzMTdiMTVjZjNmNjE5YmI5ZGFmNzkyZTI3N2NmZGE4NjU3MjlkYTUyODI3OWVjMzE4ZjgifX19");
        star.add("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTUzZGYxMWJiZjBhNzQ2MWFmMmU3MDJjNjg4NGUzZDliZjk5NzBlMDE3MmY5OTk4MTA0NDVmMzRhMGZlYWE0NyJ9fX0=");
        star.add("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODFiNjg4NGNiN2FhOWM2YTgxNDJhNWUwMzRkNjFlM2MwN2UzNDc3MWU0Mzk3ZmExMWY4ZDIxYzA0MmRmZDU3MCJ9fX0=");
        star.add("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODNiNDhkMjQ2NTgzOTNiMjkyZTk0N2M0ZTAzYTYzYjBmYjZhMDk5YjcxZTczYzg5MzdhYzQ3ODFkZjVkN2NhOSJ9fX0=");
        star.add("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDkwZTJkNWRlYzM5MWNmY2ZhYTUzMTVkN2Q5ODdjZjY1YzdlYjM0YTA5ZWRkNjU5OWFmZDE5MDEzNjJmNDNkNiJ9fX0=");
        star.add("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDczMmE0MTUzOWIzMDMzZmVmZDQzNTlhN2U0Njk5ZDMxOTVhZDdiODU1NWUwZDRiMjkxYWVkMTYxZmFhMDllYiJ9fX0=");
    }

}

package io.github.clamentos.grapher.auth.monitoring.logging;

///
import io.github.clamentos.grapher.auth.persistence.entities.Log;

///..
import io.github.clamentos.grapher.auth.persistence.repositories.LogRepository;

///.
import java.io.IOException;

///..
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

///..
import java.time.LocalDateTime;
import java.time.ZoneId;

///..
import java.time.format.DateTimeFormatter;

///..
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

///..
import java.util.regex.Pattern;

///.
import lombok.extern.slf4j.Slf4j;

///.
import org.springframework.beans.factory.annotation.Autowired;

///..
import org.springframework.core.env.Environment;

///..
import org.springframework.dao.DataAccessException;

///..
import org.springframework.scheduling.annotation.Scheduled;

///..
import org.springframework.stereotype.Component;

///
/**
 * <h3>Database Logs Writer</h3>
 * Spring {@link Component} to periodically write the log files into the {@code LOG} database table.
*/

///
@Component
@Slf4j

///
public class DatabaseLogsWriter {

    ///
    private final LogRepository logRepository;
    private final String logsPath;

    ///..
    private final Pattern pattern;
    private final DateTimeFormatter formatter;

    ///
    /** This class is a Spring bean and this constructor should never be called explicitly. */
    @Autowired
    public DatabaseLogsWriter(LogRepository logRepository, Environment environment) {

        this.logRepository = logRepository;
        logsPath = environment.getProperty("grapher-auth.logsPath", String.class);

        pattern = Pattern.compile("\\|");
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS");
    }

    ///
    @Scheduled(cron = "1/60 * * * * *")
    private void dump() {

        log.info("Starting logs dumping task...");

        try { Thread.sleep(2000); }
        catch(InterruptedException exc) { log.warn("Interrupted"); }

        int totalLogsWritten = 0;
        int totalFilesCleaned = 0;

        try {

            boolean[] halt = new boolean[]{false}; // Used as an "indirect" variable.

            List<Path> paths = Files

                .list(Paths.get(logsPath))
                .filter(path -> Files.isDirectory(path) == false)
                .sorted((first, second) -> second.getFileName().compareTo(first.getFileName()))
                .skip(1)
                .toList()
            ;

            if(halt[0] == false) {

                for(Path path : paths) {

                    List<String> lines = Files.readAllLines(path);
                    this.write(lines);
                    Files.delete(path);

                    totalLogsWritten += lines.size();
                    totalFilesCleaned++;
                }
            }
        }

        catch(IOException | RuntimeException exc) {

            log.error("Could not process files, will abort the job", exc);
        }

        if(totalFilesCleaned > 0) {

            log.info("Logs dumping task completed, written {} logs over {} files", totalLogsWritten, totalFilesCleaned);
        }
    }

    ///..
    private void write(Collection<String> lines) throws DataAccessException, NullPointerException, NumberFormatException {

        List<Log> logs = new ArrayList<>(lines.size());
        long now = System.currentTimeMillis();

        for(String line : lines) {

            String[] sections = pattern.split(line); // section[0] is the initial '|'
            long timestamp = LocalDateTime.parse(sections[1], formatter).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

            logs.add(new Log(0, timestamp, sections[2].trim(), sections[3], sections[4], sections[5], now));
        }

        logRepository.saveAll(logs);
    }

    ///
}

package studying.withsolid.model;

import java.time.LocalDate;
import java.time.LocalTime;

public final class Report {
    private final String title;
    private final LocalDate date;
    private final LocalTime time;
    private final Integer carsSold;
    private final Integer motorcyclesSold;

    private Report(Builder builder) {
        this.title = builder.title;
        this.date = builder.date;
        this.time = builder.time;
        this.carsSold = builder.carsSold;
        this.motorcyclesSold = builder.motorcyclesSold;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public Integer getCarsSold() {
        return carsSold;
    }

    public Integer getMotorcyclesSold() {
        return motorcyclesSold;
    }

    public static final class Builder {
        private String title;
        private LocalDate date;
        private LocalTime time;
        private Integer carsSold;
        private Integer motorcyclesSold;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public Builder time(LocalTime time) {
            this.time = time;
            return this;
        }

        public Builder carsSold(Integer carsSold) {
            this.carsSold = carsSold;
            return this;
        }

        public Builder motorcyclesSold(Integer motorcyclesSold) {
            this.motorcyclesSold = motorcyclesSold;
            return this;
        }

        public Report build() {
            return new Report(this);
        }
    }
}

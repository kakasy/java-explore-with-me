package ru.practicum.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import ru.practicum.enums.EventStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "annotation")
    String annotation;

    @JoinColumn(name = "category_id")
    @ManyToOne
    @ToString.Exclude
    Category category;

    @CreationTimestamp
    @Column(name = "created_on")
    LocalDateTime created;

    @Column(name = "description")
    String description;

    @Column(name = "event_date")
    LocalDateTime eventDate;

    @JoinColumn(name = "initiator_id")
    @ManyToOne
    @ToString.Exclude
    User initiator;

    @Column(name = "paid")
    Boolean paid;

    @Column(name = "participant_limit")
    Integer participantLimit;

    @Column(name = "published_on")
    LocalDateTime published;

    @Column(name = "request_moderation")
    Boolean requestModeration;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    EventStatus state;

    @Column(name = "title")
    String title;

    @Embedded
    @AttributeOverride(name = "lat", column = @Column(name = "location_lat"))
    @AttributeOverride(name = "lon", column = @Column(name = "location_lon"))
    Location location;

    @Formula("(select count(r.id) " +
            "from events as e " +
            "left join requests as r ON e.id = r.event_id and r.status = 'CONFIRMED'  " +
            "where id = r.event_id)")
    int confirmedRequests;
}

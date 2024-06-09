package ru.practicum.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.enums.RequestStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @JoinColumn(name = "requester_id")
    @ManyToOne
    User requester;

    @Column(name = "created")
    LocalDateTime created;

    @JoinColumn(name = "event_id")
    @ManyToOne
    Event event;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    RequestStatus status;
}

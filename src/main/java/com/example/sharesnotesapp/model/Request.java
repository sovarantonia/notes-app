package com.example.sharesnotesapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "requests")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Request {
    @Id
    @SequenceGenerator(
            name = "req_sequence",
            sequenceName = "req_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "req_sequence"
    )
    @Column(updatable = false)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User sender;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User receiver;

    @Value("PENDING")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    private LocalDate sentAt;
}

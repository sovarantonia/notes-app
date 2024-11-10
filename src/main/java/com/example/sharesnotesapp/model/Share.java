package com.example.sharesnotesapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "shares")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Share {
    @Id
    @SequenceGenerator(
            name = "share_sequence",
            sequenceName = "share_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "share_sequence"
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

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Note sentNote;

    @Column(nullable = false)
    private LocalDate sentAt;
}
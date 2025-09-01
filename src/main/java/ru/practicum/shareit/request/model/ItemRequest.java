package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * TODO Sprint add-item-requests.
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "items")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "requestor_id")
    private long requestorId;
}
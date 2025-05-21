package studio.devbyjose.healthyme_payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "factura")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura")
    private Integer id;

    @Column(name = "numero_factura", nullable = false, unique = true, length = 20)
    private String numeroFactura;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "impuestos", nullable = false, precision = 10, scale = 2)
    private BigDecimal impuestos;

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pago", nullable = false)
    private Pago pago;
}
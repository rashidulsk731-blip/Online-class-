package com.example.util

import com.example.model.PaymentStatus
import java.util.UUID

object UpiPaymentHandler {
    const val DEFAULT_UPI_VPA = "pmshri.school@upi"
    const val MERCHANT_NAME = "PM Shri Berbhngi HS School"

    data class PaymentRequest(
        val studentId: String,
        val courseId: String,
        val courseTitle: String,
        val amount: Double = 100.0,
        val upiId: String = DEFAULT_UPI_VPA
    )

    data class PaymentResult(
        val transactionRef: String,
        val status: PaymentStatus,
        val message: String
    )

    fun createUpiIntentUri(request: PaymentRequest, txnRef: String): String {
        return "upi://pay?pa=${request.upiId}&pn=${MERCHANT_NAME.replace(" ", "%20")}&mc=8211&tid=$txnRef&tr=$txnRef&tn=${request.courseTitle.replace(" ", "%20")}&am=${request.amount}&cu=INR"
    }

    /**
     * Executes payment processing abstraction. In production this launches Intent.ACTION_VIEW with UPI uri
     * or invokes the UPI payment SDK.
     */
    fun processPayment(request: PaymentRequest, enteredUpiId: String): PaymentResult {
        if (enteredUpiId.isBlank() || !enteredUpiId.contains("@")) {
            return PaymentResult(
                transactionRef = "TXN_FAILED",
                status = PaymentStatus.FAILED,
                message = "Invalid UPI ID format. Please enter a valid UPI address (e.g. user@okaxis, user@ybl)."
            )
        }

        val txnId = "UPI_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6).uppercase()}"
        return PaymentResult(
            transactionRef = txnId,
            status = PaymentStatus.SUCCESSFUL,
            message = "Transaction Approved. Enrolled successfully in ${request.courseTitle}."
        )
    }
}

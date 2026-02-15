package com.webapp.jobportal.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.webapp.jobportal.entity.Milestone;
import com.webapp.jobportal.entity.Users;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StripeService {

        @Value("${stripe.api.key}")
        private String stripeApiKey;

        @Value("${app.base.url:http://localhost:8080}")
        private String baseUrl;

        private static final double PLATFORM_COMMISSION_PERCENT = 0.10;

        public Session createDataSession(Milestone milestone) throws StripeException {
                // Stripe.apiKey is already set in StripeConfig, but setting it here again to be
                // safe/explicit if needed,
                // though typically Config matches singleton.
                // We will rely on StripeConfig for global init, or set it per request if
                // managing multiple keys.

                SessionCreateParams params = SessionCreateParams.builder()
                                .setMode(SessionCreateParams.Mode.PAYMENT)
                                .setSuccessUrl(
                                                baseUrl + "/payment/success?session_id={CHECKOUT_SESSION_ID}&milestone_id="
                                                                + milestone.getId())
                                .setCancelUrl(baseUrl + "/payment/cancel")
                                .addLineItem(
                                                SessionCreateParams.LineItem.builder()
                                                                .setQuantity(1L)
                                                                .setPriceData(
                                                                                SessionCreateParams.LineItem.PriceData
                                                                                                .builder()
                                                                                                .setCurrency("usd")
                                                                                                .setUnitAmount((long) (milestone
                                                                                                                .getAmount()
                                                                                                                * 100)) // Amount
                                                                                                                        // in
                                                                                                                        // cents
                                                                                                .setProductData(
                                                                                                                SessionCreateParams.LineItem.PriceData.ProductData
                                                                                                                                .builder()
                                                                                                                                .setName(milestone
                                                                                                                                                .getDescription())
                                                                                                                                .build())
                                                                                                .build())
                                                                .build())
                                .putMetadata("milestone_id", String.valueOf(milestone.getId()))
                                .putMetadata("contract_id", String.valueOf(milestone.getContract().getId()))
                                .build();

                return Session.create(params);
        }

        // Transfer funds to the freelancer's connected Stripe account, deducting
        // platform commission
        public void transferFunds(Users freelancer, Double grossAmount) throws StripeException {
                if (freelancer.getStripeAccountId() == null) {
                        throw new IllegalArgumentException("Freelancer does not have a connected Stripe account.");
                }

                long grossAmountCents = (long) (grossAmount * 100);
                long commissionCents = (long) (grossAmountCents * PLATFORM_COMMISSION_PERCENT);
                long transferAmountCents = grossAmountCents - commissionCents;

                Map<String, Object> transferParams = new HashMap<>();
                transferParams.put("amount", transferAmountCents);
                transferParams.put("currency", "usd");
                transferParams.put("destination", freelancer.getStripeAccountId());
                transferParams.put("transfer_group", "JOB_PAYMENT_" + System.currentTimeMillis());

                com.stripe.model.Transfer.create(transferParams);
        }

        // Refund the payment to the client
        public void refundPayment(String stripeId) throws StripeException {
                // Determine if usage is Session ID or PaymentIntent ID
                String paymentIntentId = stripeId;

                if (stripeId.startsWith("cs_")) {
                        Session session = Session.retrieve(stripeId);
                        paymentIntentId = session.getPaymentIntent();
                }

                Map<String, Object> params = new HashMap<>();
                params.put("payment_intent", paymentIntentId);

                com.stripe.model.Refund.create(params);
        }

        public com.stripe.model.PaymentIntent createPaymentIntent(Milestone milestone) throws StripeException {
                com.stripe.param.PaymentIntentCreateParams params = com.stripe.param.PaymentIntentCreateParams.builder()
                                .setAmount((long) (milestone.getAmount() * 100)) // Amount in cents
                                .setCurrency("usd")
                                .setAutomaticPaymentMethods(
                                                com.stripe.param.PaymentIntentCreateParams.AutomaticPaymentMethods
                                                                .builder()
                                                                .setEnabled(true)
                                                                .build())
                                .putMetadata("milestone_id", String.valueOf(milestone.getId()))
                                .putMetadata("contract_id", String.valueOf(milestone.getContract().getId()))
                                .putMetadata("job_id",
                                                String.valueOf(milestone.getContract().getJobApplication().getJob()
                                                                .getJobPostId()))
                                .build();

                return com.stripe.model.PaymentIntent.create(params);
        }

        public com.stripe.model.Account createExpressAccount(Users user) throws StripeException {
                com.stripe.param.AccountCreateParams params = com.stripe.param.AccountCreateParams.builder()
                                .setType(com.stripe.param.AccountCreateParams.Type.EXPRESS)
                                .setCountry("US") // Defaulting to US for now, can be dynamic
                                .setEmail(user.getEmail())
                                .setCapabilities(
                                                com.stripe.param.AccountCreateParams.Capabilities.builder()
                                                                .setCardPayments(
                                                                                com.stripe.param.AccountCreateParams.Capabilities.CardPayments
                                                                                                .builder()
                                                                                                .setRequested(true)
                                                                                                .build())
                                                                .setTransfers(
                                                                                com.stripe.param.AccountCreateParams.Capabilities.Transfers
                                                                                                .builder()
                                                                                                .setRequested(true)
                                                                                                .build())
                                                                .build())
                                .build();

                return com.stripe.model.Account.create(params);
        }

        public com.stripe.model.AccountLink createAccountLink(String accountId) throws StripeException {
                com.stripe.param.AccountLinkCreateParams params = com.stripe.param.AccountLinkCreateParams.builder()
                                .setAccount(accountId)
                                .setRefreshUrl(baseUrl + "/freelancer-dashboard/stripe-refresh")
                                .setReturnUrl(baseUrl + "/freelancer-dashboard/stripe-return")
                                .setType(com.stripe.param.AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                                .build();

                return com.stripe.model.AccountLink.create(params);
        }
}

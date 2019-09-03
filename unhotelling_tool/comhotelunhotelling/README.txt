Based On the specification:
https://jira.hcom/browse/CKO-1020

The tool need an imput file in the same directory:
sample.txt

contens (translator key) should be in the follow format:
e.g.:

]
"confirmation.mail.greeting.prepay.with_installment",
"confirmation.crossSell.bookAgain.text",
"booking.bookingForm.mandatoryFee.hotelCollected.hover.singleFee.crossCurrency.mandatoryFee",
"fastbooking.submit.label.book.alipay.text",
"confirmation.mail.wr_module.reduced_nightsfree_star",
]

or the sample.txt contains initial values by default

Run the tool:

mvn clean compile
or
mvn exec:java
or
Main method can be run from IDE as well

it will generate an excel table (temp.xls) based on the specification above

Enjoy
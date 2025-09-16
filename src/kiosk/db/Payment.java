package kiosk.db;

public enum Payment {
	CARD("카드"), CASH("현금"), APPPAY("앱페이");

    private final String label;

    Payment(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

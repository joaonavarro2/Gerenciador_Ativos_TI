package br.com.stecar.stecar_backend.dto;

public class DashboardResumoDTO {

    private long totalAssets;
    private long pendingMovements;
    private long maintenanceOpen;
    private long itemsInAnalysis;

    public DashboardResumoDTO() {
    }

    public DashboardResumoDTO(
            long totalAssets,
            long pendingMovements,
            long maintenanceOpen,
            long itemsInAnalysis) {

        this.totalAssets = totalAssets;
        this.pendingMovements = pendingMovements;
        this.maintenanceOpen = maintenanceOpen;
        this.itemsInAnalysis = itemsInAnalysis;
    }

    public long getTotalAssets() {
        return totalAssets;
    }

    public void setTotalAssets(long totalAssets) {
        this.totalAssets = totalAssets;
    }

    public long getPendingMovements() {
        return pendingMovements;
    }

    public void setPendingMovements(long pendingMovements) {
        this.pendingMovements = pendingMovements;
    }

    public long getMaintenanceOpen() {
        return maintenanceOpen;
    }

    public void setMaintenanceOpen(long maintenanceOpen) {
        this.maintenanceOpen = maintenanceOpen;
    }

    public long getItemsInAnalysis() {
        return itemsInAnalysis;
    }

    public void setItemsInAnalysis(long itemsInAnalysis) {
        this.itemsInAnalysis = itemsInAnalysis;
    }

}
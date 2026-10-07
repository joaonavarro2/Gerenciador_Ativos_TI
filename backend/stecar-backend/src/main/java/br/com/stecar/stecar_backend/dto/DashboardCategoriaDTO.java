package br.com.stecar.stecar_backend.dto;

public class DashboardCategoriaDTO {

    private String name;
    private double value;

    public DashboardCategoriaDTO() {
    }

    public DashboardCategoriaDTO(
            String name,
            double value) {

        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
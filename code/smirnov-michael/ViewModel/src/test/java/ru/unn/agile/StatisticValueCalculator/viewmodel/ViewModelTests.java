package ru.unn.agile.StatisticValueCalculator.viewmodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ViewModelTests {
    private ViewModel viewModel;

    @Before
    public void setUp() {
        viewModel = new ViewModel();
    }

    @Test
    public void checkSetDefaultValuesWhenJustStarted() {
        assertEquals(viewModel.nameOfCalculatedStatisticProperty().get(), "");
        assertEquals(viewModel.valueOfCalculatedStatisticProperty().get(), "");
        assertEquals(viewModel.inputRowErrorProperty().get(), InputNote.VALID_INPUT);
        assertEquals(viewModel.inputStatisticParameterErrorProperty().get().toString(), "");
        assertEquals(viewModel.selectedStatisticProperty().get(), StatisticValue.ENUMERATION);
        assertEquals(viewModel.inputRowProperty().get(), "1.0");
        assertEquals(viewModel.inputStatisticParameterProperty().get(), "0.0");

        ObservableList<StatisticValue> statisticsList =
                FXCollections.observableArrayList(StatisticValue.values());
        assertEquals(viewModel.getListOfAvailableStatistics(), statisticsList);
    }

    @Test
    public void selectedStatisticIsEqualToCentralMomentWhenCentralMomentSelected() {
        StatisticValue selectedStatistic = StatisticValue.CENTRAL_MOMENT;

        viewModel.selectedStatisticProperty().set(selectedStatistic);

        assertEquals(viewModel.selectedStatisticProperty().get(), selectedStatistic);
    }

    @Test
    public void statisticParameterFieldIsHidedWhenSelectedStatisticIsNotProbability() {
        viewModel.setSelectedStatistic(StatisticValue.ENUMERATION);

        assertFalse(viewModel.inputStatisticParameterFieldIsVisibleProperty().get());
    }

    @Test
    public void statisticParameterFieldIsShowedWhenProbabilitySelected() {
        viewModel.setSelectedStatistic(StatisticValue.PROBABILITY);

        assertTrue(viewModel.inputStatisticParameterFieldIsVisibleProperty().get());
    }

    @Test
    public void addRowErrorIsHidedWhenAddRowFieldContainsNumber() {
        viewModel.inputRowProperty().set("225");

        assertEquals(viewModel.inputRowErrorProperty().get(), InputNote.VALID_INPUT);
    }

    @Test
    public void addRowErrorIsShownWhenAddRowFieldContainsNotNumber() {
        viewModel.inputRowProperty().set("abc123");

        assertEquals(viewModel.getInputRowError(), InputNote.NOT_A_NUMBER);
    }

    @Test
    public void addRowErrorDisappearWhenResetNumberToAddRowField() {
        viewModel.inputRowProperty().set("abc123");
        viewModel.inputRowProperty().set("123");

        assertEquals(viewModel.inputRowErrorProperty().get(), InputNote.VALID_INPUT);
    }

    @Test
    public void addStatisticParameterErrorIsHidedWhenAddRowFieldContainsNumber() {
        viewModel.inputStatisticParameterProperty().set("111");

        assertEquals(viewModel.inputStatisticParameterErrorProperty().get(),
                InputNote.VALID_INPUT);
    }

    @Test
    public void addStatisticParameterErrorIsShownWhenProbabilitySelectedAndEventIsNotANumber() {
        viewModel.selectedStatisticProperty().set(StatisticValue.PROBABILITY);
        viewModel.inputStatisticParameterProperty().set("abc123");

        assertEquals(viewModel.inputStatisticParameterErrorProperty().get(),
                InputNote.NOT_A_NUMBER);
    }

    @Test
    public void orderInputErrorIsShownWhenRawMomentChosenAndOrderIsNotPositiveInteger() {
        viewModel.selectedStatisticProperty().set(StatisticValue.ROW_MOMENT);
        viewModel.inputStatisticParameterProperty().set("-1.2");

        assertEquals(viewModel.inputStatisticParameterErrorProperty().get(),
                InputNote.NOT_A_POSITIVE_INTEGER);
    }

    @Test
    public void addStatisticParameterErrorDisappearWhenResetNumberToAddRowField() {
        viewModel.inputStatisticParameterProperty().set("abc123");
        viewModel.inputStatisticParameterProperty().set("123");

        assertEquals(viewModel.getInputStatisticParameterError(),
                InputNote.VALID_INPUT);
    }

    @Test
    public void newValueIsSavedIntoDataTableWhenNewRowAdded() {
        String newRow = "3.14";

        viewModel.setInputRow(newRow);
        viewModel.addRowToStatisticData();

        Integer sizeOfData = viewModel.getStatisticData().size();
        Pair<String, String> lastRowInData = viewModel.getStatisticData().get(sizeOfData - 1);

        assertEquals(newRow, lastRowInData.getValue());
    }

    @Test
    public void addNewRowToDataTableIsNotAllowedWhenInputRowIsNotValid() {
        viewModel.inputRowProperty().set("abc");

        assertTrue(viewModel.addInputRowIsDisabledProperty().get());
    }

    @Test
    public void calculatingStatisticIsDisabledWhenDataIsCleared() {
        viewModel.clearStatisticData();

        assertTrue(viewModel.calculationIsDisabledProperty().get());
    }

    @Test
    public void calculatingStatisticIsDisabledWhenOrderOfRawMomentIsZero() {
        viewModel.selectedStatisticProperty().set(StatisticValue.ROW_MOMENT);
        viewModel.inputStatisticParameterProperty().set("0");

        assertTrue(viewModel.calculationIsDisabledProperty().get());
    }

    @Test
    public void calculatingStatisticIsEnabledWhenParameterValueIsNotNumberAndEnumerationChosen() {
        viewModel.selectedStatisticProperty().set(StatisticValue.ENUMERATION);
        viewModel.inputStatisticParameterProperty().set("abc");

        assertFalse(viewModel.getCalculationIsDisabled());
    }

    @Test
    public void deleteRowFromDataTableIsDisabledWhenRowInTableNotSelected() {
        viewModel.makeRowInDataNotSelected();

        assertTrue(viewModel.deleteDataRowIsDisabledProperty().get());
    }

    @Test
    public void deleteRowFromDataTableIsEnabledWhenRowInTableSelected() {
        setUpDataTable();

        viewModel.selectRowInStatisticData(1);

        assertFalse(viewModel.getDeleteDataRowIsDisabled());
    }

    @Test
    public void secondRowInDataTableDeletedAfterSelectAndDeleteSecondRow() {
        setUpDataTable();

        viewModel.selectRowInStatisticData(1);
        viewModel.deleteSelectedRowInStatisticData();

        ObservableList<Pair<String, String>> dataTable = viewModel.getStatisticData();

        boolean hasDataTableOnlyOneElement = dataTable.size() == 1;
        boolean isFirstElementInDataTableEqualsToOne = dataTable.get(0).getValue().equals("1.0");
        assertTrue(hasDataTableOnlyOneElement && isFirstElementInDataTableEqualsToOne);
    }

    @Test
    public void dataTableIsEmptyAfterClearDataTable() {
        setUpDataTable();

        viewModel.clearStatisticData();

        ObservableList<Pair<String, String>> dataTable = viewModel.getStatisticData();
        assertTrue(dataTable.isEmpty());
    }

    @Test
    public void varianceDescriptionIsSelectedAfterCalculatingVariance() {
        viewModel.setSelectedStatistic(StatisticValue.VARIANCE);
        viewModel.calculateSelectedStatistic();
        String nameOfCalculatedStatistic = viewModel.nameOfCalculatedStatisticProperty().get();

        assertEquals(nameOfCalculatedStatistic, StatisticValue.VARIANCE.toString());
    }

    @Test
    public void enumerationValueIsShownAfterCalculatingEnumeration() {
        viewModel.setSelectedStatistic(StatisticValue.ENUMERATION);
        viewModel.calculateSelectedStatistic();

        assertFalse(viewModel.getValueOfCalculatedStatistic().isEmpty());
    }

    @Test
    public void parameterNameIsEqualToOrderWhenRowMomentSelected() {
        viewModel.setSelectedStatistic(StatisticValue.ROW_MOMENT);

        assertEquals(viewModel.parameterNameOfSelectedStatisticProperty().get().toString(),
                StatisticParameter.ORDER.toString());
    }

    @Test
    public void parameterNameIsEqualToEventWhenProbabilitySelected() {
        viewModel.setSelectedStatistic(StatisticValue.PROBABILITY);

        assertEquals(viewModel.getParameterNameOfSelectedStatistic(),
                StatisticParameter.EVENT);
    }

    @Test
    public void calculationIsDisabledWhenSetEmptyStatisticData() {
        ObservableList<Pair<String, String>> emptyData = FXCollections.emptyObservableList();
        viewModel.setStatisticData(emptyData);

        assertTrue(viewModel.calculationIsDisabledProperty().get());
    }

    @Test
    public void deleteRowInDataIsDisabledWhenSelectedRowNumberIsOutOfBounds() {
        viewModel.selectRowInStatisticData(-1);

        assertTrue(viewModel.deleteDataRowIsDisabledProperty().get());
    }

    @Test
    public void nameOfCalculatedStatisticIsEqualsToRowMomentAfterCalculateRowMomentOfSecondOrder() {
        viewModel.setSelectedStatistic(StatisticValue.ROW_MOMENT);
        viewModel.inputStatisticParameterProperty().setValue("2");
        viewModel.calculateSelectedStatistic();

        assertEquals(viewModel.getNameOfCalculatedStatistic(),
                StatisticValue.ROW_MOMENT.toString());
    }

    @Test
    public void nameOfCalculatedStatisticIsEqualsToProbabilityAfterCalculateProbability() {
        viewModel.setSelectedStatistic(StatisticValue.PROBABILITY);
        viewModel.inputStatisticParameterProperty().setValue("-1.2");
        viewModel.calculateSelectedStatistic();

        assertEquals(viewModel.getNameOfCalculatedStatistic(),
                StatisticValue.PROBABILITY.toString());
    }

    @Test
    public void addRowToDataTableIsDisabledWhenInputValueIsEmpty() {
        viewModel.inputRowProperty().set("");

        assertTrue(viewModel.getAddInputRowIsDisabled());
    }

    @Test
    public void calculatingIsDisabledWhenStatisticParameterValueIsEmptyAndCentralMomentSelected() {
        viewModel.setSelectedStatistic(StatisticValue.CENTRAL_MOMENT);
        viewModel.inputStatisticParameterProperty().set("");

        assertTrue(viewModel.calculationIsDisabledProperty().get());
    }

    @Test
    public void calculatingIsEnabledWhenStatisticParameterValueIsEmptyAndVarianceSelected() {
        viewModel.setSelectedStatistic(StatisticValue.VARIANCE);
        viewModel.inputStatisticParameterProperty().set("");

        assertFalse(viewModel.calculationIsDisabledProperty().get());
    }

    private void setUpDataTable() {
        ArrayList<String> statistics = new ArrayList<>();
        statistics.add("1.0");
        statistics.add("2.0");

        ObservableList<Pair<String, String>> dataTable = FXCollections.observableArrayList();
        for (Integer i = 1; i <= statistics.size(); i++) {
            dataTable.add(new Pair<>(i.toString(), statistics.get(i - 1)));
        }
        viewModel.setStatisticData(dataTable);
    }
}

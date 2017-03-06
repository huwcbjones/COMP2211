package t16.interfaces;

import javafx.util.Pair;

import java.util.List;

/**
 * {DESCRIPTION}
 *
 * @author Huw Jones
 * @since 06/03/2017
 */
public interface IChartRenderer {


    void render(List<Pair<String, Number>> data);
}

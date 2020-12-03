package com.codenjoy.dojo.tetris.client;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2016 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.codenjoy.dojo.client.AbstractJsonSolver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.tetris.model.Elements;

/**
 * User: your name
 * Это твой алгоритм AI для игры. Реализуй его на свое усмотрение.
 * Обрати внимание на {@see YourSolverTest} - там приготовлен тестовый
 * фреймворк для тебя.
 */
public class YourSolver extends AbstractJsonSolver<Board> {

    private Dice dice;

    public YourSolver() {
    }

    public YourSolver(Dice dice) {
        this.dice = dice;
    }

    @Override
    public String getAnswer(Board board) {
        List<Command> answerList = getAnswerList(board);
        List<String> stringList = answerList.stream().map(Command::toString).collect(toList());
        return String.join(", ", stringList);
    }

    private List<Command> getAnswerList(Board board) {
        // список команд
        List<Command> result = new ArrayList<>();

        final GlassBoard glass = board.getGlass();
        final List<Elements> futureFigures = board.getFutureFigures();
        final List<Point> freeSpace = glass.getFreeSpace();
        final Elements currentFigureType = board.getCurrentFigureType();
        System.out.println(freeSpace);

        Point theLeftestPoint = getPointForFigure(glass, currentFigureType);
        System.out.println("The leftest point is " + theLeftestPoint);


        final Point currentFigurePoint = board.getCurrentFigurePoint();
        int directionDelta = theLeftestPoint.getX() - currentFigurePoint.getX();

        if (directionDelta < 0) {
            for (int i = 0; i > directionDelta; i--) {
                result.add(Command.LEFT);
            }
        } else {
            for (int i = 0; i < directionDelta; i++) {
                result.add(Command.RIGHT);
            }
        }

//        if (directionDelta == 0) {
            for (int i = 0; i < currentFigurePoint.getY(); i++) {
                result.add(Command.DOWN);
            }
//        }


        return result;
    }

    private Point getPointForFigure(GlassBoard glass, Elements currentFigureType) {

        final List<Point> freeSpace = glass.getFreeSpace();

        Point theLeftestPoint = null;

        for (int i = 0; i < 18; i++) {
            for (Point point : freeSpace) {
                if (theLeftestPoint == null
                        && point.getY() == i
                        && isApplicable(freeSpace, point, currentFigureType)) {
                    theLeftestPoint = point;
                } else if (theLeftestPoint != null
                        && point.getX() < theLeftestPoint.getX()
                        && point.getY() == i
                        && isApplicable(freeSpace, point, currentFigureType)) {
                    theLeftestPoint = point;
                }
            }
            if (theLeftestPoint != null) {
                break;
            }
        }

        return theLeftestPoint;
    }

    private boolean isApplicable(List<Point> freeSpace, Point theLeftestPoint, Elements currentFigureType) {

        switch (currentFigureType) {
            case YELLOW: {
                Point topLeft = new PointImpl();
                topLeft.setY(theLeftestPoint.getY() + 1);
                topLeft.setX(theLeftestPoint.getX());

                Point topRight = new PointImpl();
                topRight.setY(theLeftestPoint.getY() + 1);
                topRight.setX(theLeftestPoint.getX() + 1);

                Point bottomRight = new PointImpl();
                bottomRight.setY(theLeftestPoint.getY());
                bottomRight.setX(theLeftestPoint.getX() + 1);

                if (freeSpace.contains(topLeft)
                        && freeSpace.contains(topRight)
                        && freeSpace.contains(bottomRight)) {
                    return true;
                }
                break;
            }
            case BLUE: {
                Point first = new PointImpl();
                first.setY(theLeftestPoint.getY() + 3);
                first.setX(theLeftestPoint.getX());

                Point second = new PointImpl();
                second.setY(theLeftestPoint.getY() + 2);
                second.setX(theLeftestPoint.getX());

                Point third = new PointImpl();
                third.setY(theLeftestPoint.getY() + 1);
                third.setX(theLeftestPoint.getX());

                if (freeSpace.contains(first)
                        && freeSpace.contains(second)
                        && freeSpace.contains(third)) {
                    return true;
                }
                break;
            }
        }


        return false;
    }


    public static void main(String[] args) {
        WebSocketRunner.runClient(
                // скопируйте сюда адрес из браузера, на который перейдете после регистрации/логина
                "http://codebattle2020.westeurope.cloudapp.azure.com/codenjoy-contest/board/player/93pm48ynmdljab4cegh1?code=6366567532785197631&gameName=tetris",
                new YourSolver(),
                new Board());
    }

}

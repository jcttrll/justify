/*
 * Copyright 2018-2019 the Justify authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.leadpony.justify.cli;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.leadpony.justify.cli.Console.Color;

/**
 * The entry class of this program.
 *
 * @author leadpony
 */
public class Launcher {

    public Status launch(String[] args) {
        return launch(new LinkedList<>(Arrays.asList(args)));
    }

    private Status launch(List<String> args) {
        Console console = createConsole();
        try {
            Command command = createCommand(args, console);
            return command.execute(args);
        } catch (CommandException e) {
            console.withColor(Color.DANGER).error(e);
            return Status.FAILED;
        } catch (Exception e) {
            console.withColor(Color.DANGER).error(e);
            return Status.FAILED;
        }
    }

    private static Console createConsole() {
        return new ColorConsole(System.out, System.err);
    }

    private static Command createCommand(List<String> args, Console console) {
        if (args.isEmpty() || args.contains("-h")) {
            return new Help(console);
        }
        return new Validate(console);
    }

    /**
     * The entry point of this program.
     *
     * @param args the arguments given to this program.
     */
    public static void main(String[] args) {
        System.exit(new Launcher().launch(args).code());
    }
}

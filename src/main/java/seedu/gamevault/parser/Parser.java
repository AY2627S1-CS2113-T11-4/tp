package seedu.gamevault.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.gamevault.command.AddCommand;
import seedu.gamevault.command.Command;
import seedu.gamevault.command.DeleteCommand;
import seedu.gamevault.command.EditCommand;
import seedu.gamevault.command.ExitCommand;
import seedu.gamevault.command.FilterCommand;
import seedu.gamevault.command.FilterTagCommand;
import seedu.gamevault.command.FindCommand;
import seedu.gamevault.command.HelpCommand;
import seedu.gamevault.command.ListCommand;
import seedu.gamevault.command.ListTagsCommand;
import seedu.gamevault.command.PickCommand;
import seedu.gamevault.command.SortCommand;
import seedu.gamevault.command.TagCommand;
import seedu.gamevault.command.UntagCommand;
import seedu.gamevault.exception.GameVaultException;
import seedu.gamevault.game.Game;
import seedu.gamevault.game.SortOrder;
import seedu.gamevault.game.Tag;

/**
 * Turns a line typed by the user into the matching {@link Command}.
 * The first word of the line is the command word (case-insensitive); the rest are its arguments.
 */
public class Parser {
    private static final Logger logger = Logger.getLogger(Parser.class.getName());
    private static final String MIN_PREFIX = "/min";
    private static final String MAX_PREFIX = "/max";
    private static final String TITLE_PREFIX = "/title";
    private static final String TIME_PREFIX = "/time";
    private static final String TAG_PREFIX = "/tag";
    private static final String NO_TIME_VALUE = "none";
    /** Key under which {@link #splitByPrefixes} stores the text that comes before the first prefix. */
    private static final String PREAMBLE = "";

    /**
     * Returns the command that matches the user's input.
     *
     * @param input Full line typed by the user.
     * @return The command to execute.
     * @throws GameVaultException If the input is empty, the command word is unknown,
     *     or a command that takes no arguments was given some.
     */
    public static Command parse(String input) throws GameVaultException {
        assert input != null : "Input passed to Parser should not be null";

        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            throw new GameVaultException("Please enter a command. Type 'help' to see all commands.");
        }

        // Split into the command word and everything after it, e.g. "filter 4" -> ["filter", "4"].
        String[] parts = trimmedInput.split("\\s+", 2);
        String commandWord = parts[0].toLowerCase();
        String arguments = parts.length > 1 ? parts[1] : "";
        logger.log(Level.FINE, "Parsing command word: {0}", commandWord);

        switch (commandWord) {
        case HelpCommand.COMMAND_WORD:
            checkNoArguments(commandWord, arguments);
            return new HelpCommand();
        case ExitCommand.COMMAND_WORD:
            checkNoArguments(commandWord, arguments);
            return new ExitCommand();
        case AddCommand.COMMAND_WORD:
            return parseAdd(arguments);
        case ListCommand.COMMAND_WORD:
            checkNoArguments(commandWord, arguments);
            return new ListCommand();
        case FilterCommand.COMMAND_WORD:
            return parseFilter(arguments);
        case DeleteCommand.COMMAND_WORD:
            return parseDelete(arguments);
        case FindCommand.COMMAND_WORD:
            return parseFind(arguments);
        case EditCommand.COMMAND_WORD:
            return parseEdit(arguments);
        case TagCommand.COMMAND_WORD:
            return parseTag(arguments);
        case UntagCommand.COMMAND_WORD:
            return parseUntag(arguments);
        case ListTagsCommand.COMMAND_WORD:
            checkNoArguments(commandWord, arguments);
            return new ListTagsCommand();
        case PickCommand.COMMAND_WORD:
            return parsePick(arguments);
        case SortCommand.COMMAND_WORD:
            return parseSort(arguments);
        default:
            throw new GameVaultException("Unknown command '" + parts[0]
                    + "'. Type 'help' to see all commands.");
        }
    }

    /**
     * Rejects extra text after a command that takes no arguments, so that a typo like
     * "exit now" is reported instead of being silently accepted.
     */
    private static void checkNoArguments(String commandWord, String arguments) throws GameVaultException {
        if (!arguments.isEmpty()) {
            throw new GameVaultException("The '" + commandWord + "' command does not take any arguments.");
        }
    }

    /**
     * Builds an {@link AddCommand} from arguments in the form
     * {@code TITLE /min MIN /max MAX [/time MINUTES] [/tag TAG [MORE_TAGS]...]},
     * e.g. {@code Catan /min 3 /max 4 /time 60 /tag strategy}. The prefixes may come in any order after the title.
     *
     * @throws GameVaultException If a field is missing or repeated, a number is not a whole number,
     *     a number is less than 1, MAX is less than MIN, or a tag is invalid.
     */
    private static Command parseAdd(String arguments) throws GameVaultException {
        Map<String, String> fields = splitByPrefixes(arguments, MIN_PREFIX, MAX_PREFIX, TIME_PREFIX, TAG_PREFIX);
        if (!fields.containsKey(MIN_PREFIX) || !fields.containsKey(MAX_PREFIX)) {
            throw new GameVaultException("Please use the format: " + AddCommand.USAGE
                    + " (e.g. add Catan /min 3 /max 4 /time 60)");
        }

        String title = fields.get(PREAMBLE);
        if (title.isEmpty()) {
            throw new GameVaultException("TITLE is missing. Format: " + AddCommand.USAGE);
        }
        // The data file uses '|' to separate fields, so a title containing it could not be loaded back.
        if (title.contains("|")) {
            throw new GameVaultException("TITLE cannot contain the '|' character.");
        }
        int minPlayers = parsePositiveNumber(fields.get(MIN_PREFIX), "MIN", AddCommand.USAGE);
        int maxPlayers = parsePositiveNumber(fields.get(MAX_PREFIX), "MAX", AddCommand.USAGE);
        if (maxPlayers < minPlayers) {
            throw new GameVaultException("MAX (" + maxPlayers + ") cannot be less than MIN (" + minPlayers + ").");
        }
        Game game;
        if (!fields.containsKey(TIME_PREFIX)) {
            game = new Game(title, minPlayers, maxPlayers);
        } else {
            int playTime = parsePositiveNumber(fields.get(TIME_PREFIX), "MINUTES", AddCommand.USAGE);
            game = new Game(title, minPlayers, maxPlayers, playTime);
        }
        if (fields.containsKey(TAG_PREFIX)) {
            for (String tag : parseTagList(fields.get(TAG_PREFIX), AddCommand.USAGE)) {
                game.addTag(tag);
            }
        }
        return new AddCommand(game);
    }

    /**
     * Builds a {@link FilterCommand} from arguments in the form {@code COUNT [/time MINUTES]},
     * e.g. {@code 4} or {@code 4 /time 45}, or a {@link FilterTagCommand} from arguments in the form
     * {@code [COUNT] [/time MINUTES] /tag TAG}, e.g. {@code /tag party} or {@code 6 /tag party}.
     *
     * @throws GameVaultException If COUNT is missing, a number is not a whole number or is less than 1,
     *     /time is repeated, or the /tag form is invalid.
     */
    private static Command parseFilter(String arguments) throws GameVaultException {
        Map<String, String> fields = splitByPrefixes(arguments, TIME_PREFIX, TAG_PREFIX);
        if (fields.containsKey(TAG_PREFIX)) {
            return parseFilterByTag(fields);
        }
        int playerCount = parsePositiveNumber(fields.get(PREAMBLE), "COUNT", FilterCommand.USAGE);
        if (!fields.containsKey(TIME_PREFIX)) {
            return new FilterCommand(playerCount);
        }
        int minutes = parsePositiveNumber(fields.get(TIME_PREFIX), "MINUTES", FilterCommand.USAGE);
        return new FilterCommand(playerCount, minutes);
    }

    /**
     * Splits arguments such as {@code Catan /min 3 /max 4} into the text before the first prefix
     * (stored under {@link #PREAMBLE}) and the text after each prefix (stored under the prefix itself).
     * A prefix only counts when it stands alone as a word, so a title like {@code Foo/minimal} is not split.
     * Prefixes that do not appear are left out of the result.
     *
     * @param arguments Arguments typed after the command word.
     * @param prefixes Prefixes this command accepts, e.g. {@code /min}.
     * @return Trimmed text for the preamble and for each prefix found.
     * @throws GameVaultException If a prefix appears more than once.
     */
    private static Map<String, String> splitByPrefixes(String arguments, String... prefixes)
            throws GameVaultException {
        // Matches any of the prefixes when it has whitespace (or the start/end of the text) on both sides.
        Pattern prefixPattern = Pattern.compile("(?<!\\S)(" + String.join("|", prefixes) + ")(?!\\S)");
        Matcher matcher = prefixPattern.matcher(arguments);

        Map<String, String> fields = new HashMap<>();
        String currentKey = PREAMBLE;
        int valueStart = 0;
        while (matcher.find()) {
            fields.put(currentKey, arguments.substring(valueStart, matcher.start()).trim());
            currentKey = matcher.group(1);
            if (fields.containsKey(currentKey)) {
                throw new GameVaultException(currentKey + " can only be given once.");
            }
            valueStart = matcher.end();
        }
        fields.put(currentKey, arguments.substring(valueStart).trim());
        return fields;
    }

    //@@author SwaggyDaddy67
    /**
     * Builds a {@link FindCommand} from arguments in the form {@code KEYWORD}, e.g. {@code mono}.
     * The keyword may contain spaces, e.g. {@code ticket to}, and is matched as one piece of text.
     *
     * @throws GameVaultException If KEYWORD is missing.
     */
    private static Command parseFind(String arguments) throws GameVaultException {
        String keyword = arguments.trim();
        if (keyword.isEmpty()) {
            throw new GameVaultException("KEYWORD is missing. Format: " + FindCommand.USAGE
                    + " (e.g. find cat)");
        }
        return new FindCommand(keyword);
    }

    //@@author
    /**
     * Builds a {@link PickCommand} from arguments in the form {@code COUNT [/time MINUTES]},
     * e.g. {@code 4} or {@code 4 /time 45}.
     *
     * @throws GameVaultException If COUNT is missing, a number is not a whole number or is less than 1,
     *     or /time is repeated.
     */
    private static Command parsePick(String arguments) throws GameVaultException {
        Map<String, String> fields = splitByPrefixes(arguments, TIME_PREFIX);
        int playerCount = parsePositiveNumber(fields.get(PREAMBLE), "COUNT", PickCommand.USAGE);
        if (!fields.containsKey(TIME_PREFIX)) {
            return new PickCommand(playerCount);
        }
        int minutes = parsePositiveNumber(fields.get(TIME_PREFIX), "MINUTES", PickCommand.USAGE);
        return new PickCommand(playerCount, minutes);
    }

    //@@author SwaggyDaddy67
    /**
     * Builds a {@link DeleteCommand} from arguments in the form {@code INDEX}, e.g. {@code 2}.
     *
     * @throws GameVaultException If INDEX is missing, is not a whole number, or is less than 1.
     */
    private static Command parseDelete(String arguments) throws GameVaultException {
        String indexText = arguments.trim();
        if (indexText.isEmpty()) {
            throw new GameVaultException("INDEX is missing. Format: " + DeleteCommand.USAGE
                    + " (e.g. delete 2)");
        }
        return new DeleteCommand(parseIndex(indexText));
    }

    //@@author
    /**
     * Returns the game number in {@code indexText}, which must be a whole number of at least 1.
     * Shared by commands that pick a game by its number in {@code list}, e.g. delete and edit.
     *
     * @throws GameVaultException If the text is not a whole number, or is less than 1.
     */
    private static int parseIndex(String indexText) throws GameVaultException {
        int displayNumber;
        try {
            displayNumber = Integer.parseInt(indexText);
        } catch (NumberFormatException e) {
            // Digits that still cannot be parsed form a number too big to fit in an int,
            // e.g. "99999999999", so report what is really wrong instead of "not a whole number".
            if (indexText.matches("-\\d+")) {
                throw new GameVaultException(getIndexTooSmallMessage(indexText));
            }
            if (indexText.matches("\\+?\\d+")) {
                throw new GameVaultException(DeleteCommand.getNoSuchGameMessage(indexText));
            }
            throw new GameVaultException("INDEX should be a whole number, but was '" + indexText + "'.");
        }
        if (displayNumber < 1) {
            throw new GameVaultException(getIndexTooSmallMessage(indexText));
        }
        return displayNumber;
    }
    //@@author Xandurs
    /**
     * Builds an {@link EditCommand} from arguments in the form
     * {@code INDEX [/title TITLE] [/min MIN] [/max MAX] [/time MINUTES|none]},
     * e.g. {@code 2 /max 6 /title Catan} or {@code 3 /time none}.
     * At least one field must be given, and fields may be in any order.
     *
     * @throws GameVaultException If INDEX is missing or invalid, no field is given,
     *         a field is repeated, or a field's value is invalid.
     */
    private static Command parseEdit(String arguments) throws GameVaultException {
        Map<String, String> fields = splitByPrefixes(arguments, TITLE_PREFIX, MIN_PREFIX, MAX_PREFIX, TIME_PREFIX);

        String indexText = fields.get(PREAMBLE);
        if (indexText.isEmpty()) {
            throw new GameVaultException("INDEX is missing. Format: " + EditCommand.USAGE
                    + " (e.g. edit 2 /max 6)");
        }
        int displayNumber = parseIndex(indexText);

        String title = fields.containsKey(TITLE_PREFIX)
                ? parseTitle(fields.get(TITLE_PREFIX), EditCommand.USAGE)
                : null;
        Integer minPlayers = fields.containsKey(MIN_PREFIX)
                ? parsePositiveNumber(fields.get(MIN_PREFIX), "MIN", EditCommand.USAGE)
                : null;
        Integer maxPlayers = fields.containsKey(MAX_PREFIX)
                ? parsePositiveNumber(fields.get(MAX_PREFIX), "MAX", EditCommand.USAGE)
                : null;
        Integer playTime = fields.containsKey(TIME_PREFIX)
                ? parseEditPlayTime(fields.get(TIME_PREFIX))
                : null;

        if (title == null && minPlayers == null && maxPlayers == null && playTime == null) {
            throw new GameVaultException("Please give at least one field to change. Format: "
                    + EditCommand.USAGE + " (e.g. edit 2 /max 6)");
        }
        // MAX vs MIN can be checked here when both are given; otherwise EditCommand checks it
        // against the game's current values.
        if (minPlayers != null && maxPlayers != null && maxPlayers < minPlayers) {
            throw new GameVaultException("MAX (" + maxPlayers + ") cannot be less than MIN (" + minPlayers + ").");
        }
        logger.log(Level.FINE, "Parsed edit of game " + displayNumber);
        return new EditCommand(displayNumber, title, minPlayers, maxPlayers, playTime);
    
    }
    //@@author Xandurs
    /**
     * Returns the play time given to {@code /time} in an edit: a number of minutes (at least 1),
     * or {@link EditCommand#CLEAR_PLAY_TIME} when the user typed {@code none} (in any case).
     *
     * @throws GameVaultException If the value is missing, or is neither {@code none} nor a whole number of at least 1.
     */
    private static int parseEditPlayTime(String text) throws GameVaultException {
        if (text.trim().equalsIgnoreCase(NO_TIME_VALUE)) {
            return EditCommand.CLEAR_PLAY_TIME;
        }
        return parsePositiveNumber(text, "MINUTES", EditCommand.USAGE);
    }

    //@@author SwaggyDaddy67
    /**
     * Builds a {@link SortCommand} from arguments in the form {@code ORDER}, e.g. {@code time}.
     *
     * @throws GameVaultException If ORDER is missing or is not one of the supported orders.
     */
    private static Command parseSort(String arguments) throws GameVaultException {
        String orderText = arguments.trim();
        if (orderText.isEmpty()) {
            throw new GameVaultException("Please say what to sort by: " + SortOrder.describeKeywords()
                    + ". Format: " + SortCommand.USAGE + " (e.g. sort time)");
        }
        Optional<SortOrder> order = SortOrder.fromKeyword(orderText);
        if (order.isEmpty()) {
            throw new GameVaultException("Cannot sort by '" + orderText + "'. Choose "
                    + SortOrder.describeKeywords() + ". Format: " + SortCommand.USAGE);
        }
        return new SortCommand(order.get());
    }

    //@@author KRAZYZECTRON
    /**
     * Builds a {@link TagCommand} from arguments in the form {@code INDEX TAG [MORE_TAGS]...},
     * e.g. {@code 2 party word}.
     *
     * @throws GameVaultException If INDEX or TAG is missing or invalid.
     */
    private static Command parseTag(String arguments) throws GameVaultException {
        String[] parts = splitIndexAndTag(arguments, TagCommand.USAGE, "tag 2 party word");
        return new TagCommand(parseIndex(parts[0]), parseTagList(parts[1], TagCommand.USAGE));
    }

    /**
     * Builds an {@link UntagCommand} from arguments in the form {@code INDEX TAG [MORE_TAGS]...},
     * e.g. {@code 2 party word}.
     *
     * @throws GameVaultException If INDEX or TAG is missing or invalid.
     */
    private static Command parseUntag(String arguments) throws GameVaultException {
        String[] parts = splitIndexAndTag(arguments, UntagCommand.USAGE, "untag 2 party");
        return new UntagCommand(parseIndex(parts[0]), parseTagList(parts[1], UntagCommand.USAGE));
    }

    /**
     * Splits arguments such as {@code 2 party word} into the INDEX text and the text of the tags after it.
     *
     * @param usage Format of the command being parsed, shown when a part is missing.
     * @param example A correct use of the command, shown when a part is missing.
     * @return A two-element array: the INDEX text, then the tags text.
     * @throws GameVaultException If INDEX or TAG is missing.
     */
    private static String[] splitIndexAndTag(String arguments, String usage, String example)
            throws GameVaultException {
        String[] parts = arguments.trim().split("\\s+", 2);
        if (parts[0].isEmpty()) {
            throw new GameVaultException("INDEX and TAG are missing. Format: " + usage + " (e.g. " + example + ")");
        }
        if (parts.length < 2) {
            throw new GameVaultException("TAG is missing. Format: " + usage + " (e.g. " + example + ")");
        }
        return parts;
    }

    /**
     * Builds a {@link FilterTagCommand} from the fields of a {@code filter} command that used /tag.
     * COUNT and /time are optional here, so {@code filter /tag party} shows every party game while
     * {@code filter 6 /time 30 /tag party} narrows them down. Only one tag can be given.
     *
     * @param fields Fields returned by {@link #splitByPrefixes}, which include /tag.
     * @throws GameVaultException If TAG is missing or invalid, or COUNT or MINUTES is invalid.
     */
    private static Command parseFilterByTag(Map<String, String> fields) throws GameVaultException {
        String tag = parseTagText(fields.get(TAG_PREFIX), FilterTagCommand.USAGE);
        String countText = fields.get(PREAMBLE);
        int playerCount = countText.isEmpty()
                ? 0
                : parsePositiveNumber(countText, "COUNT", FilterTagCommand.USAGE);
        int minutes = fields.containsKey(TIME_PREFIX)
                ? parsePositiveNumber(fields.get(TIME_PREFIX), "MINUTES", FilterTagCommand.USAGE)
                : 0;
        return new FilterTagCommand(tag, playerCount, minutes);
    }

    /**
     * Returns the tags in {@code text}, which are separated by spaces, normalised and with repeats removed,
     * e.g. {@code Party word party} gives {@code [party, word]}.
     *
     * @param usage Format of the command being parsed, shown when no tag is given.
     * @return The tags in the order they were typed; never empty.
     * @throws GameVaultException If no tag is given or any tag does not follow {@link Tag#RULES}.
     */
    private static List<String> parseTagList(String text, String usage) throws GameVaultException {
        if (text.isBlank()) {
            throw new GameVaultException("TAG is missing. Format: " + usage);
        }
        // A LinkedHashSet drops repeated tags while keeping the order the user typed them in.
        Set<String> tags = new LinkedHashSet<>();
        for (String word : text.trim().split("\\s+")) {
            tags.add(parseTagText(word, usage));
        }
        return new ArrayList<>(tags);
    }

    /**
     * Returns the tag in {@code text}, normalised to lower case (see {@link Tag#normalise}).
     *
     * @param usage Format of the command being parsed, shown when the tag is missing.
     * @throws GameVaultException If the tag is missing or does not follow {@link Tag#RULES}.
     */
    private static String parseTagText(String text, String usage) throws GameVaultException {
        String tag = Tag.normalise(text);
        if (tag.isEmpty()) {
            throw new GameVaultException("TAG is missing. Format: " + usage);
        }
        if (!Tag.isValid(tag)) {
            throw new GameVaultException("TAG should be " + Tag.RULES + ", but was '" + text.trim() + "'.");
        }
        return tag;
    }

    private static String parseTitle(String text, String usage) throws GameVaultException {
        String title = text.trim();
        if (title.isEmpty()) {
            throw new GameVaultException("TITLE is missing. Format: " + usage);
        }
        // The data file uses '|' to separate fields, so a title containing it could not be loaded back.
        if (title.contains("|")) {
            throw new GameVaultException("TITLE cannot contain the '|' character.");
        }
        return title;
    }

    //@@author

    private static void checkNotRepeated(Object existingValue, String prefix) throws GameVaultException {
        if (existingValue != null) {
            throw new GameVaultException(prefix + " was given more than once.");
        }
    }

    //@@author SwaggyDaddy67
    private static String getIndexTooSmallMessage(String indexText) {
        return "INDEX should be at least 1, but was " + indexText + ".";
    }

    //@@author
    /**
     * Returns the number in {@code text}, such as a player count or a number of minutes,
     * which must be a whole number of at least 1.
     *
     * @param fieldName Name of the field (e.g. MIN, MAX, COUNT or MINUTES), used in error messages.
     * @param usage Format of the command being parsed, shown when the field is missing.
     */
    private static int parsePositiveNumber(String text, String fieldName, String usage) throws GameVaultException {
        String trimmedText = text.trim();
        if (trimmedText.isEmpty()) {
            throw new GameVaultException(fieldName + " is missing. Format: " + usage);
        }
        int count;
        try {
            count = Integer.parseInt(trimmedText);
        } catch (NumberFormatException e) {
            throw new GameVaultException(fieldName + " should be a whole number, but was '" + trimmedText + "'.");
        }
        if (count < 1) {
            throw new GameVaultException(fieldName + " should be at least 1, but was " + count + ".");
        }
        return count;
    }
}

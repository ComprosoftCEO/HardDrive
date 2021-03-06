package game.scenes;

/**
 * Level 2: Desert
 */
public class Level2 extends GameScene {

  public Level2() {
    super("sand-bg", 2, 2);
  }

  @Override
  protected String[] getLevelLayout() {
    return new String[] {
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                r          r           r                                        A                                                                                                                       ",
        "                                                                                                                                                                                     |                  ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                     ^              ^                                                                   ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                 #                                                                                                                                                      ",
        "                                                                                                                                                                   0                                    ",
        "                           -                                                                   *                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                             |                                                                          ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "          ^                                                                                                                                                                                             ",
        "                                                                                                                                                                                                        ",
        "                                                                           -                                                                                                                            ",
        "                                                 *                                                                                                *                                                     ",
        "                                                                                                          A                                                                                             ",
        "                                                                                                                                                                                                        ",
        "                r          r         r                                                                                                                                                                  ",
        "                                                                                                                                                                              ^                         ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                   0                        ^                                                                                                                           ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                       #                                                                ",
        "                                                                        r          r            r                                                                                                       ",
        "                        ^     ^                                                                                                                                                                         ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                   |                    ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                 a                                                                                                                                      ",
        "                @                                                                                                                                                                                       ",
        "                                                                                                                                                                                     0                  ",
        "                                                                                                               @                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                         ^                                                                                                                                              ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                              @                                         ",
        "                         R    R                                                                                                                                                                         ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                    |                                                                   ",
        "                                                                                                                                                                                                        ",
        "                                                                          r                                                                                                                             ",
        "                                        #                                                                                                                                                               ",
        "                                                                                                    ^                                                                                                   ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                            #                           ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                                                               R                                                                                        ",
        "                               r                                                     R                                                                                                                  ",
        "                                                                                                                                                                                |                       ",
        "                                                                                                                                             @                                                          ",
        "                                                                                                                                                                                                        ",
        "                                                                                                    s                                                                                                   ",
        "                                                                                                                                                                                                        ",
        "               A                                   @                                                                                                                                                    ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                                                               R                                                                                        ",
        "                                                    -                                R                                                                                                                  ",
        "                                                                                                                                         |                                                              ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                                         @                                                                                                              ",
        "                       ^                                                           @                                                                                                                    ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "            |                                                                  @ @ @ @ @ @ @                                                                     a                                      ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                 0                                                                      ",
        "                                                                                @  @  @  @                                                                                                              ",
        "                                                  |                                                                                                                                                     ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                          ^                                                             ",
        "                                                                                                                                                                                                        ",
        "            |                                                                                                                                                                                           ",
        "                                                                                                                                                                                0                       ",
        "                                  *                                                                                                                                                                     ",
        "                                                                                                                                                                                                        ",
        "                                                                               A                   *                                                                                                    ",
        "                                                                                                                                                      @                                                 ",
        "                                                                                                                                                   @     @                                              ",
        "                                     0                     #                                                                                          @                                                 ",
        "                                                                                                                                                   @     @                                              ",
        "                                                                                                                                                      @                                                 ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                   @                                                                                                                                                                                    ",
        "                                                                                                                                                                         a                              ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                               @ @ @ @ @ @ @                                                     *                                                                      ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "          |                                                                                                                                                                                             ",
        "                                                                                                                                                                                                        ",
        "                                                                                                  0                                                                                                     ",
        "                                                                                                                                                                                      *                 ",
        "                                        A                                                                                                                                                               ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                                  -                                                                                                                     ",
        "                                                                                                                                                                                                        ",
        "                       @                                                                             ^         ^        ^                                                  @                            ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                      ^                                                        #                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                   @@@@@@@@@                            ",
        "                                                                                                                                                                                                        ",
        "                                    0                                                                                                                                                                   ",
        "                                                                                                                                                                                                        ",
        "                                                                                 |                                                              ^                                                       ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
        "                                                                                                 @@@@@@@@@@@@                                                                                           ",
        "            *                                                                                                                                                                                           ",
        "                                      @@@@@@@@@@@@@                                                                                                                                                     ",
        "                                                                                                                                                                                                        ",
        "                                                                                                                                                                                                        ",
    };
  }
}

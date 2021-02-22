# style
  检查和格式化源代码中的空白元素，使之符合如下要求：

  1. 使用空格代替tab缩进
  2. 每行源代码不能使用空格结尾
  3. 每个源文件需要使用空行结尾
  4. 源文件需要在头部声明许可证

## 1. maven
  使用maven时，声明在pom.xml中的plugins元素中，用以在打包环节进行检查，格式是否符合要求。

    <plugin>
      <groupId>org.beangle</groupId>
      <artifactId>style-maven-plugin</artifactId>
      <version>0.0.2</version>
      <executions>
        <execution>
          <goals>
            <goal>check</goal>
          </goals>
        </execution>
      </executions>
    </plugin>

## 2. sbt
  使用sbt构建时，在project/plugins.sbt中添加

    addSbtPlugin("org.beangle.style" % "sbt-beangle-style" % "0.0.2")

  在编译过程中，会检查上属规则。手工格式化代码，可以使用

    styleFormatAll


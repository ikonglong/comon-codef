该库提供一个简单的，协议无关的 Error 模型，这使我们能够跨不同 API，不同 API 协议（例如 gRPC 或 HTTP）和不同错误
上下文（例如异步，批处理或工作流错误）提供一致的错误处理经验。该库实现了 [Google Cloud API Design Guide](https://cloud.google.com/apis/design)
的 Errors 部分，并根据组织的实际情况做了一些调整（引入了 Case 概念）。

## 核心概念

### Status

代表错误模型，属性有：

- code: status code
- name: status 名称，或叫标题。可读性比 code 好
- theCase: 具体错误情形标识符，可以是数字字符串或可读性更好的词组字符串。可选
- message: 描述具体错误情形。可选
- details: 客户端代码可用来处理错误的额外错误信息，例如重试信息或帮助链接。

### Case

如果用户关心一些比 Status 更具体的错误情形，那么就定义一些 Case 来代表这些情形。 具体错误情形有如下属性：

- identifier: 唯一标识一种具体错误情形，是 Status.theCase 的取值。
- statusCode: 当前 Case 被映射到哪个 status code

Case 有两种表示风格：

- 数字编码表示风格。
  提供了开箱即用的默认实现 [DigitCodedCase](https://github.com/ikonglong/common-codef/master/core/src/main/java/com/github/ikonglong/common/status/DigitCodedCase.java)
  。用户也可以实现 Case 接口完全自定义编码方案。
- 单词编码表示风格。目前未提供开箱即用的相关组件，因为比较简单

举个例子，下单操作可能发生「库存不足」这种情形，可用 **01_01_052** 数字编码风格的 Case 表示，也可用 **INSUFFICIENT_INVENTORY** 单词编码风格的 case
表示。

### Error Mapping

我们可能在不同的编程环境中访问同一组 API。典型地，每个编程环境都有自己处理错误的方式。这部分描述在通常采用的编程环境中错误模型如何被映射。

#### HTTP Mapping

对于 JSON HTTP API，error schema:

```
{
  "code": <int: status code>, # Required
  "status": <string: status name/title", # Required
  "message": <string: an descriptive message>", # Optional
  "theCase": <string: the identifier of the case that ocurred> # Optional
}
```

建议将 HTTP response status 设置为 `status.code().toHttpStatus().code()`;

### Handling Errors

下表是 HTTP Status 跟 Status 的对应关系，以及对 Status 的简短描述。

| HTTP Status               | Response Status       | Description                                                  |
| ------------------------- | --------------------- | ------------------------------------------------------------ |
| OK:200                    | OK:0                  | 无错误。                                                     |
| BAD_REQUEST:400           | INVALID_ARGUMENT:3    | 客户端指定了无效参数。如需了解详情，请查看错误消息和错误详细信息。 |
| BAD_REQUEST:400           | FAILED_PRECONDITION:9 | 请求无法在当前系统状态下执行，例如删除非空目录。             |
| BAD_REQUEST:400           | OUT_OF_RANGE:11       | 客户端指定了无效范围。                                       |
| UNAUTHORIZED:401          | UNAUTHENTICATED:16    | 由于 OAuth 令牌丢失、无效或过期，请求未通过身份验证。        |
| FORBIDDEN:403             | PERMISSION_DENIED:7   | 客户端权限不足。可能的原因包括 OAuth 令牌的作用域不包括当前 API、客户端没有权限，或者尚未为客户端项目启用当前 API 等。 |
| NOT_FOUND:404             | NOT_FOUND:5           | 找不到指定的资源，或者请求由于未公开/披露的原因（例如白名单）而被拒绝。 |
| CONFLICT:409              | ALREADY_EXISTS:6      | 客户端尝试创建的资源已存在。                                 |
| CONFLICT:409              | ABORTED:10            | 并发冲突，例如 read-modify-write 冲突。                      |
| TOO_MANY_REQUESTS:429     | RESOURCE_EXHAUSTED:8  | 资源配额不足或达到速率限制。                                 |
| CLIENT_CLOSED_REQUEST:499 | CANCELLED:1           | 请求被客户端取消。                                           |
| INTERNAL_SERVER_ERROR:500 | DATA_LOSS:15          | 出现不可恢复的数据丢失或数据损坏。客户端应该向用户报告该错误。 |
| INTERNAL_SERVER_ERROR:500 | UNKNOWN:2             | 未知的服务端错误。通常是服务端 bug。                         |
| INTERNAL_SERVER_ERROR:500 | INTERNAL:13           | 服务端内部错误。通常是服务端 bug。                           |
| NOT_IMPLEMENTED:501       | UNIMPLEMENTED:12      | 服务端未实现当前 API                                         |
| SERVICE_UNAVAILABLE:503   | UNAVAILABLE:14        | 服务不可用。通常是服务器宕机了，或者网络抖动。               |
| TIMEOUT:504               | DEADLINE_EXCEEDED:4   | 超出请求时限。仅当调用者设置的时限比方法的默认时限短（即请求的时限不足以让服务器处理请求）并且请求未在调用者期望的时限内完成时，才会发生这种情况。 |

关于每种错误该采取怎样的重试策略，你的 API
服务应该如何传播内部依赖服务报告给你的错误，请看 [handling errors](https://cloud.google.com/apis/design/errors#handling_errors)
部分。

Client 拿到 status 后，调用 `status.retryAdvice()` 获取重试建议。

## 如何使用

### 选择合适的 status

Sometimes multiple error codes may apply. Services should return the most specific error code that
applies. For example, prefer `OUT_OF_RANGE` over `FAILED_PRECONDITION` if both codes apply.
Similarly prefer `NOT_FOUND` or `ALREADY_EXISTS` over `FAILED_PRECONDITION`.

### 何时应该定义 Case

大多数情形下，使用 status code 和 message 就够了。例如，一个包含很多字段的请求，每个字段都有自己的校验逻辑。但我们没必要为每个字段定义一个 case
以表明是哪个字段校验失败，返回 INVALID_ARGUMENT status，以及描述哪个字段违反了什么约束的 message 就够了。因为通常 Client 端也会校验，由 Client
的校验组件负责提供更精确的错误提示更好。

如果一个操作执行过程中可能发生的多个 error case 对应于同一个 status，且 End User 关心这些 case，那么就应该定义具体的 case，并将它们作为 API
定义的一部分。举个例子，下单操作可能发生库存不足、超过限购情形，它们都对应于 FAILED_PRECONDITION status；当发生这些情形时也需要告知最终用户， 所以 API 定义中得包含这些
case。

### 如何定义 Case

Case 有两种表示风格：数字编码表示风格、单词编码表示风格。先确定使用哪种表示风格。

#### 定义数字编码表示风格的 Case

DigitCodedCase 由三部分组成：

- appCode: 应用编码，为下一级 moduleCode 提供编码空间。可选
- moduleCode: 模块编码，为下一级 conditionCode 提供编码空间。可选
- conditionCode: 在上一级编码空间中唯一标识一种错误情形。必需

该库提供了三个开箱即用的 case 工厂：

- BasicDigitCodedCaseFactory.FactoryForMonolithicApp: 适用于内部没有划分模块的单体应用  
  使用该工厂的样例 [MonolithicAppExample](https://github.com/ikonglong/common-codef/master/examples/src/main/java/com/github/ikonglong/common/status/example/MonolithicAppExample.java)
- BasicDigitCodedCaseFactory.FactoryForMultiModuleApp: 适用于多模块应用，模块数量不多余 10  
  使用该工厂的样例 [MultiModuleAppExample](https://github.com/ikonglong/common-codef/master/examples/src/main/java/com/github/ikonglong/common/status/example/MultiModuleAppExample.java)
- BasicDigitCodedCaseFactory.FactoryForComplexSystem: 适用于包含多个多模块应用的复杂系统

当然，你也可以扩展 BasicDigitCodedCaseFactory 定义自己的 case
factory。请看样例 [DigitCodedCaseExample](https://github.com/ikonglong/common-codef/master/examples/src/main/java/com/github/ikonglong/common/status/example/DigitCodedCaseExample.java)

这三个开箱即用的 case factory 使用了相同的 StatusCodeMapper 组件。该组件将 condition code segment 映射至一个 status，令该 segment
包含的 code 个数为 n，则对于该 status，就可以定义 n 个更具体的 case。默认提供的组件中，condition code 取值范围为 [0, 999]，0 映射至 ok
status，然后每 50 个数字划分为一个 segment。condition code segment 到 status 的映射如下：

| Condition Code Segment | Status | HTTP Status |
| ------ | ------ | ------ |
| [0, 0] | OK:0 | OK:200 |
| [1, 50] | INVALID_ARGUMENT:3 | BAD_REQUEST:400 |
| [51, 100] | FAILED_PRECONDITION:9 | BAD_REQUEST:400 |
| [101, 150] | OUT_OF_RANGE:11 | BAD_REQUEST:400 |
| [151, 200] | UNAUTHENTICATED:16 | UNAUTHORIZED:401 |
| [201, 250] | PERMISSION_DENIED:7 | FORBIDDEN:403 |
| [251, 300] | NOT_FOUND:5 | NOT_FOUND:404 |
| [301, 350] | ALREADY_EXISTS:6 | CONFLICT:409 |
| [351, 400] | ABORTED:10 | CONFLICT:409 |
| [401, 450] | RESOURCE_EXHAUSTED:8 | TOO_MANY_REQUESTS:429 |
| [451, 500] | CANCELLED:1 | CLIENT_CLOSED_REQUEST:499 |
| [501, 550] | DATA_LOSS:15 | INTERNAL_SERVER_ERROR:500 |
| [551, 600] | UNKNOWN:2 | INTERNAL_SERVER_ERROR:500 |
| [601, 650] | INTERNAL:13 | INTERNAL_SERVER_ERROR:500 |
| [651, 700] | UNIMPLEMENTED:12 | NOT_IMPLEMENTED:501 |
| [701, 750] | UNAVAILABLE:14 | SERVICE_UNAVAILABLE:503 |
| [751, 800] | DEADLINE_EXCEEDED:4 | TIMEOUT:504 |

如果想自定义 condition code 取值范围、分段、以及到 status 的映射，扩展 StatusCodeMapper 组件即可。

#### 定义单词编码风格的 Case

目前未提供开箱即用的工厂组件。请看样例 [WordCodedCaseExample](https://github.com/ikonglong/common-codef/master/examples/src/main/java/com/github/ikonglong/common/status/example/WordCodedCaseExample.java)

package org.hime.core

import org.hime.cast
import org.hime.parse.*
import java.util.*

fun eval(ast: ASTNode, symbolTable: SymbolTable): Token {
    var temp = ast.tok
    while (true)
        temp = if (temp.type == Type.ID && symbolTable.contains(cast<String>(temp.value)))
            symbolTable.get(cast<String>(temp.value))
        else
            break
    ast.tok = temp
    if ((ast.isEmpty() && ast.type != AstType.FUNCTION))
        return ast.tok
    if (ast.tok.type == Type.STATIC_FUNCTION) {
        ast.tok = cast<Hime_StaticFunction>(ast.tok.value)(ast, symbolTable)
        ast.clear()
        return ast.tok
    }
    for (i in 0 until ast.size())
        ast[i].tok = eval(ast[i].copy(), symbolTable)
    val parameters = ArrayList<Token>()
    for (i in 0 until ast.size())
        parameters.add(ast[i].tok)
    if (ast.tok.type == Type.FUNCTION) {
        ast.tok = cast<Hime_Function>(ast.tok.value)(parameters, symbolTable)
        ast.clear()
        return ast.tok
    }
    if (ast.tok.type == Type.HIME_FUNCTION) {
        ast.tok = cast<Hime_HimeFunction>(ast.tok.value)(parameters)
        ast.clear()
        return ast.tok
    }
    return ast.tok
}

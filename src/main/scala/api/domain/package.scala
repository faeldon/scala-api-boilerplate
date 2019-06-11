package api

package object domain extends domain.DomainModel {

  object implicits
    extends domain.DomainModelInstances
    with domain.DomainModelCodecs
    with domain.DomainModelSchema

  object syntax
    extends domain.DomainModelSyntax
    with domain.DomainModelValSyntax

}
